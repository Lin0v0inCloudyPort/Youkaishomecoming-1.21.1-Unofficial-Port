package dev.xkmc.youkaishomecoming.content.attachment.graze;

import dev.xkmc.danmakuapi.content.spell.item.SpellContainer;
import dev.xkmc.fastprojectileapi.collision.EntityStorageHelper;
import dev.xkmc.l2core.capability.player.PlayerCapabilityTemplate;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import dev.xkmc.youkaishomecoming.content.entity.youkai.YoukaiEntity;
import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import dev.xkmc.youkaishomecoming.init.data.GLModConfig;
import dev.xkmc.youkaishomecoming.init.registrate.GLMeta;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SerialClass
public class GrazeCapability extends PlayerCapabilityTemplate<GrazeCapability> {

	private static final int MAX_GRAZE = 100, SHARD = 5, CYCLE = 3;
	private static final int GRAZE_CACHE = 10;

	@SerialField
	private int power, hidden, step, bomb, life, invul, weak;
	@SerialField
	private Map<UUID, CombatSession> sessions = new LinkedHashMap<>();

	private boolean dirty = false;
	private int tempGraze = 0;
	private int lastGraze = 0;

	@Override
	public void onClone(Player player, boolean isWasDeath) {
		if (isWasDeath) {
			power = 0;
			hidden = 0;
			step = 0;
			invul = 0;
			life = 0;
			bomb = 0;
			weak = 0;
			// The player lost the danmaku battle (died); drop any lingering sessions so they respawn
			// with a clean state instead of an empty-life session that would re-trigger the battle UI.
			sessions.clear();
			dirty = true;
		}
	}

	public void initStatus(Player player) {
		int initResource = GrazeHelperGL.getInitialResource(player) * SHARD;
		int initPower = GrazeHelperGL.getInitialPower(player) * MAX_GRAZE;
		life = Math.max(initResource, life);
		bomb = Math.max(initResource, bomb);
		power = Math.max(initPower, power);
	}

	@Override
	public void tick(Player player) {
		boolean full = GLCharacterHelper.isFullCharacter(player);
		if (tempGraze > 0) {
			tempGraze--;
		double val = GrazeHelperGL.getGrazeEffectiveness(player);
			int count = (int) val;
			if (player.getRandom().nextFloat() < val - count) count++;
			for (int i = 0; i < count; i++)
				consumeGraze(player);
			dirty = true;
		}
		if (invul > 0) invul--;
		if (weak > 0) weak--;
		int maxPower = GrazeHelperGL.getMaxPower(player) * MAX_GRAZE;
		int maxResource = GrazeHelperGL.getMaxResource(player) * SHARD;
		if (power > maxPower) power = maxPower;
		if (life > maxResource) life = maxResource;
		if (bomb > maxResource) bomb = maxResource;
		if (player.level() instanceof ServerLevel sl) {
			if (!full) {
				dirty = !sessions.isEmpty();
				sessions.clear();
			} else {
				for (var ent : new ArrayList<>(sessions.entrySet())) {
					if (ent.getValue().youkai == null) dirty = true;
					if (ent.getValue().shouldRemove(sl, player)) {
						sessions.remove(ent.getKey());
						dirty = true;
					}
				}
			}
			if (dirty)
				sync(player);
		}
		dirty = false;
		if (player.level().isClientSide) {
			dev.xkmc.danmakuapi.api.GrazeHelper.globalInvulTime = invul;
			dev.xkmc.danmakuapi.api.GrazeHelper.globalForbidTime = Math.max(invul, weak);
		}
	}

	public boolean graze(Player player) {
		if (invul > 0) return false;
		if (!GLCharacterHelper.isFullCharacter(player)) return false;
		if (tempGraze < GRAZE_CACHE)
			tempGraze++;
		boolean ans = player.tickCount != lastGraze;
		lastGraze = player.tickCount;
		return ans;
	}

	private void consumeGraze(Player player) {
		if (power < GrazeHelperGL.getMaxPower(player) * MAX_GRAZE) {
			power++;
			return;
		}
		if (sessions.isEmpty()) return;
		hidden++;
		if (hidden < MAX_GRAZE) return;
		hidden -= MAX_GRAZE;
		step++;
		int max = GrazeHelperGL.getMaxResource(player) * SHARD;
		if (step == CYCLE) {
			if (life < max) {
				life++;
				step = 0;
			} else if (bomb < max) {
				bomb++;
				step--;
			} else {
				step--;
			}
		} else {
			if (bomb < max) bomb++;
			else if (life < max) step++;
		}
	}

	public HitType performErase(Player player, YoukaiEntity e) {
		if (!GLCharacterHelper.isFullCharacter(player)) return HitType.NONE;
		if (!sessions.containsKey(e.getUUID())) return HitType.ERASE;
		if (invul > 0) return HitType.INVUL;
		for (var s : sessions.values()) {
			s.eraseDanmaku(player);
		}
		if (useBomb(player)) {
			return HitType.BOMB;
		}
		// Resource (life/bomb) is exhausted. Instead of ending the battle (resetTarget + weak), which
		// left the youkai's aggro in a broken state (re-entering combat but never firing, player stuck
		// invulnerable), the danmaku battle continues and bullets now deal real damage until one side
		// falls. We still clear the screen on hit (ERASE), but skip the invul grant so the hit lands.
		if (life < SHARD) {
			int maxLoss = (int) (GLModConfig.SERVER.maxPowerLossOnMiss.get() * MAX_GRAZE);
			power -= Math.min(power / 2, maxLoss);
			dirty = true;
			if (player instanceof ServerPlayer sp) {
				GensokyoLegacy.HANDLER.toClientPlayer(new GrazeToClient(1), sp);
				SpellContainer.clear(sp);
			}
			return HitType.ERASE;
		}
		int maxLoss = (int) (GLModConfig.SERVER.maxPowerLossOnMiss.get() * MAX_GRAZE);
		int loss = Math.min(power / 2, maxLoss);
		power -= loss;
		dirty = true;
		invul = GLModConfig.SERVER.missInvulTime.get();
		if (player instanceof ServerPlayer sp) {
			GensokyoLegacy.HANDLER.toClientPlayer(new GrazeToClient(1), sp);
			SpellContainer.clear(sp);
		}
		life -= SHARD;
		bomb = GrazeHelperGL.getInitialResource(player) * SHARD;
		return HitType.LIFE;
	}

	public boolean useBomb(Player player) {
		if (bomb < SHARD) return false;
		bomb -= SHARD;
		invul = GLModConfig.SERVER.bombInvulTime.get();
		dirty = true;
		return true;
	}

	public float powerBonus(Player player) {
		if (!GLCharacterHelper.isFullCharacter(player)) return 0;
		int support = power / MAX_GRAZE;
		return support * GLModConfig.SERVER.danmakuPowerBonus.get().floatValue();
	}

	public List<InfoLine> getInfoLines(Player player) {
		if (!GLCharacterHelper.isFullCharacter(player)) return List.of();
		var icon = new InfoIcon(
				GensokyoLegacy.loc("textures/gui/elements.png"),
				20, 20
		);
		if (sessions.isEmpty()) {
			boolean holding = player.getMainHandItem().is(GrazeHelperGL.DANMAKU_SHOOTER) ||
					player.getOffhandItem().is(GrazeHelperGL.DANMAKU_SHOOTER);
			boolean bypass = player.getAbilities().instabuild && player.isShiftKeyDown();
			if (!holding) return List.of();
			if (!bypass) {
				return List.of(new InfoLine("%.2f".formatted(power * 0.01), icon, 10, 10));
			}
		}
		var list = new java.util.ArrayList<InfoLine>();
		if (invul > 0) {
			// Invulnerability / recovery window: bullets can't hurt you, but you also can't fire.
			// Blink between yellow and white so it reads as a temporary state, with a seconds countdown.
			boolean blink = (player.tickCount / 5) % 2 == 0;
			int color = blink ? 0xffffff55 : 0xffffffff;
			list.add(new InfoLine("%.1f".formatted(invul / 20d), icon, 0, 0, color));
		}
		list.add(new InfoLine("%.1f".formatted(life * 1d / SHARD), icon, 0, 10));
		list.add(new InfoLine("%.1f".formatted(bomb * 1d / SHARD), icon, 0, 0));
		list.add(new InfoLine("%.2f".formatted(power * 1d / MAX_GRAZE), icon, 10, 10));
		list.add(new InfoLine("%.2f".formatted(hidden * 1d / MAX_GRAZE), icon, 10, 0));
		return list;
	}

	public boolean isInSession(UUID uuid) {
		return sessions.containsKey(uuid);
	}

	public void initSession(Player player, YoukaiEntity youkai) {
		if (sessions.containsKey(youkai.getUUID())) return;
		if (sessions.isEmpty()) initStatus(player);
		sessions.put(youkai.getUUID(), new CombatSession().init(youkai));
		youkai.targets.addPlayer(player);
		dirty = true;
	}

	public void stopSession(Player player, UUID uuid) {
		if (!sessions.containsKey(uuid)) return;
		sessions.remove(uuid);
		if (sessions.isEmpty() && player instanceof ServerPlayer sp) {
			SpellContainer.clear(sp);
		}
		dirty = true;
	}

	public boolean shouldHurt(Player player, LivingEntity le) {
		if (!GLCharacterHelper.isFullCharacter(player)) return true;
		if (le instanceof YoukaiEntity youkai) {
			if (weak > 0) return false;
			if (sessions.containsKey(youkai.getUUID())) return true;
			if (youkai.targets.contains(player)) return true;
			if (sessions.isEmpty()) {
				initSession(player, youkai);
				return true;
			}
			return false;
		}
		return sessions.isEmpty() || le instanceof Mob mob && mob.getTarget() == player;
	}

	public Optional<LivingEntity> findAny(Player player) {
		return sessions.values().stream().findAny().map(e -> e.getTarget(player));
	}

	public void remove(UUID uuid) {
		sessions.remove(uuid);
	}

	public void setLife(int i) {
		life = i;
		dirty = true;
	}

	public void setBomb(int i) {
		bomb = i;
		dirty = true;
	}

	public void setPower(int i) {
		power = i;
		dirty = true;
	}

	public boolean isInvul() {
		return invul > 0;
	}

	public boolean isWeak() {
		return weak > 0;
	}

	public int getLife() {
		return life;
	}

	public int getBomb() {
		return bomb;
	}

	public int getPower() {
		return power;
	}

	public void sync(Player player) {
		if (player instanceof ServerPlayer sp)
			GLMeta.GRAZE.type().network.toClient(sp);
	}

	@SerialClass
	public static class CombatSession {

		@SerialField
		private UUID uuid;
		@SerialField
		private int uid;

		private YoukaiEntity youkai;

		public CombatSession init(YoukaiEntity e) {
			uuid = e.getUUID();
			uid = e.getId();
			youkai = e;
			return this;
		}

		public boolean shouldRemove(ServerLevel sl, Player player) {
			if (youkai == null) {
				if (sl.getEntity(uuid) instanceof YoukaiEntity e) {
					youkai = e;
					uid = youkai.getId();
				} else return true;
			}
			if (!youkai.isAlive() || !EntityStorageHelper.isPresent(youkai))
				return true;
			return !youkai.targets.contains(player);
		}

		@Nullable
		public LivingEntity getTarget(Player player) {
			if (youkai != null) return youkai;
			return player.level().getEntity(uid) instanceof LivingEntity le ? le : null;
		}

		protected void resetTarget(Player player) {
			if (getTarget(player) instanceof YoukaiEntity e) {
				e.resetTarget(player);
			}
		}

		protected void eraseDanmaku(Player player) {
			if (getTarget(player) instanceof YoukaiEntity e) {
				e.eraseAllDanmaku(player);
			}
		}

	}

	public record InfoLine(String text, InfoIcon icon, int x, int y, int color) {

		public InfoLine(String text, InfoIcon icon, int x, int y) {
			this(text, icon, x, y, 0xffffffff);
		}

	}

	public record InfoIcon(ResourceLocation loc, int w, int h) {

	}

	public enum HitType {
		NONE, INVUL, BOMB, LIFE, ERASE, LAST;

		public boolean skipDamage() {
			return this == BOMB || this == LIFE || this == INVUL || this == LAST;
		}

		public boolean erase() {
			return this == BOMB || this == LIFE || this == ERASE || this == LAST;
		}

	}

}
