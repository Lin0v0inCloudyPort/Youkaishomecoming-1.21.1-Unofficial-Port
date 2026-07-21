package dev.xkmc.youkaishomecoming.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired when a player would lose their last life to danmaku. Cancel to keep the player alive
 * (the player retains the life and the hit is treated as a regular life loss instead).
 */
public class DanmakuLastHitEvent extends Event implements ICancellableEvent {

	public final Player player;
	public final LivingEntity attacker;

	public DanmakuLastHitEvent(Player player, LivingEntity attacker) {
		this.player = player;
		this.attacker = attacker;
	}

	public Player getPlayer() {
		return player;
	}

	public LivingEntity getAttacker() {
		return attacker;
	}

}
