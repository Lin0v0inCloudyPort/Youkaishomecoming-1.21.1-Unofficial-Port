package dev.xkmc.youkaishomecoming.content.attachment.graze;

import dev.xkmc.youkaishomecoming.init.registrate.GLSounds;
import net.minecraft.client.Minecraft;

public class GrazeClientHandler {

	public static void playGraze() {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		var r = player.getRandom();
		player.playSound(GLSounds.GRAZE.get(), r.nextFloat() * 0.2f + 1, r.nextFloat() * 0.2f + 1f);
	}

	public static void playMiss() {
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		player.playSound(GLSounds.MISS.get(), 0.7f, 1);
	}

}
