package dev.xkmc.youkaishomecoming.event;

import dev.xkmc.youkaishomecoming.content.entity.youkai.YoukaiEntity;
import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.UUID;

@EventBusSubscriber(modid = GensokyoLegacy.MODID)
public class YoukaiHatredClearHandler {

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp) {
			clearHatred(sp);
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp) {
			clearHatred(sp);
		}
	}

	@SubscribeEvent
	public static void onGameModeChange(PlayerEvent.PlayerChangeGameModeEvent event) {
		GameType mode = event.getNewGameMode();
		if (mode != GameType.CREATIVE && mode != GameType.SPECTATOR) return;
		if (event.getEntity() instanceof ServerPlayer sp) {
			clearHatred(sp);
		}
	}

	private static void clearHatred(ServerPlayer player) {
		dev.xkmc.youkaishomecoming.init.registrate.GLMeta.CHAR.type().getOrCreate(player).clearHostility(player);
		MinecraftServer server = player.getServer();
		if (server == null) return;
		UUID uuid = player.getUUID();
		for (ServerLevel sl : server.getAllLevels()) {
			for (Entity e : sl.getAllEntities()) {
				if (!(e instanceof YoukaiEntity youkai)) continue;
				clearOne(youkai, uuid);
			}
		}
	}

	private static void clearOne(YoukaiEntity youkai, UUID uuid) {
		youkai.targets.removePlayer(uuid);

		LivingEntity target = youkai.getTarget();
		if (target != null && uuid.equals(target.getUUID())) {
			youkai.setTarget(null);
		}

		Brain<?> brain = youkai.getBrain();
		brain.getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(t -> {
			if (uuid.equals(t.getUUID())) {
				brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
			}
		});

		LivingEntity lastHurtBy = youkai.getLastHurtByMob();
		if (lastHurtBy != null && uuid.equals(lastHurtBy.getUUID())) {
			youkai.setLastHurtByMob(null);
		}
	}

}
