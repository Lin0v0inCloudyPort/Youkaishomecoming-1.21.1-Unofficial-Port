package dev.xkmc.youkaishomecoming.event;

import dev.xkmc.fastprojectileapi.entity.GrazingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired when a player grazes a danmaku. Cancel to prevent the graze from being counted.
 */
public class DanmakuGrazeEvent extends Event implements ICancellableEvent {

	public final Player player;
	public final GrazingEntity danmaku;

	public DanmakuGrazeEvent(Player player, GrazingEntity danmaku) {
		this.player = player;
		this.danmaku = danmaku;
	}

	public Player getPlayer() {
		return player;
	}

	public GrazingEntity getDanmaku() {
		return danmaku;
	}

}
