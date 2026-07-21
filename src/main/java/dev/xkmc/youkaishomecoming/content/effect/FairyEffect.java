package dev.xkmc.youkaishomecoming.content.effect;

import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import dev.xkmc.youkaishomecoming.init.data.GLModConfig;
import dev.xkmc.youkaishomecoming.init.registrate.GLAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Set;

public class FairyEffect extends MobEffect {

	@SuppressWarnings("unchecked")
	public FairyEffect(MobEffectCategory category, int color) {
		super(category, color);
		var uuid = GensokyoLegacy.loc("fairy");
		addAttributeModifier(Attributes.MAX_HEALTH, uuid, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
				lv -> readConfig(GLModConfig.SERVER.fairyHealthReduction, -0.5));
		addAttributeModifier(Attributes.ATTACK_DAMAGE, GensokyoLegacy.loc("fairy_atk"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
				lv -> readConfig(GLModConfig.SERVER.fairyAttackReduction, -0.5));
		addAttributeModifier(Attributes.MOVEMENT_SPEED, GensokyoLegacy.loc("fairy_spd"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
				lv -> readConfig(GLModConfig.SERVER.fairySpeedBonus, 0.2));
		addAttributeModifier((DeferredHolder<Attribute, Attribute>) (Object) GLAttributes.HITBOX.val(), GensokyoLegacy.loc("fairy_hitbox"),
				AttributeModifier.Operation.ADD_VALUE,
				lv -> readConfig(GLModConfig.SERVER.fairyHitboxReduction, -0.5));
	}

	private static double readConfig(ModConfigSpec.DoubleValue value, double fallback) {
		try {
			return value.get();
		} catch (IllegalStateException e) {
			return fallback;
		}
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		// not curable by milk
	}

}
