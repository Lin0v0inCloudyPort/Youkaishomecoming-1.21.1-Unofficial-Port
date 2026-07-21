package dev.xkmc.youkaishomecoming.content.attachment.graze;

import dev.xkmc.l2serial.network.SerialPacketBase;
import net.minecraft.world.entity.player.Player;

/**
 * type 0 = graze sound, type 1 = miss sound.
 */
public record GrazeToClient(int type) implements SerialPacketBase<GrazeToClient> {

	@Override
	public void handle(Player player) {
		if (type == 0) GrazeClientHandler.playGraze();
		else GrazeClientHandler.playMiss();
	}

}
