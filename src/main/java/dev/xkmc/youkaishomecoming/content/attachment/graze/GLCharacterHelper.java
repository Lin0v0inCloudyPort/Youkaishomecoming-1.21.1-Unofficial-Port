package dev.xkmc.youkaishomecoming.content.attachment.graze;

import dev.xkmc.youkaishomecoming.content.entity.youkai.YoukaiEntity;
import dev.xkmc.youkaishomecoming.init.registrate.YHEffects;
import net.minecraft.world.entity.LivingEntity;

/**
 * Mirrors the "full character" gating from the original 1.20.1 EffectEventHandlers.
 * A player only participates in the graze / power / life / bomb danmaku-battle system
 * while they are a full character (youkaified or fairy), and youkai always count.
 */
public class GLCharacterHelper {

	public static boolean isFullCharacter(LivingEntity e) {
		return e instanceof YoukaiEntity ||
				e.hasEffect(YHEffects.YOUKAIFIED) ||
				e.hasEffect(YHEffects.FAIRY);
	}

}
