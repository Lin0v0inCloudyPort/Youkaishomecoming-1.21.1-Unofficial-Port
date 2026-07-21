package dev.xkmc.youkaishomecoming.content.effect;

import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import dev.xkmc.youkaishomecoming.init.data.GLModConfig;
import dev.xkmc.youkaishomecoming.init.registrate.YHEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class YoukaifiedEffect extends MobEffect {

	private static final ResourceLocation ID = GensokyoLegacy.loc("youkaified");

	public YoukaifiedEffect(MobEffectCategory category, int color) {
		super(category, color);
		this.addAttributeModifier(Attributes.MAX_HEALTH, ID, AttributeModifier.Operation.ADD_VALUE,
				lv -> readConfig(GLModConfig.SERVER.youkaifiedHealthBonus, 20.0));
		this.addAttributeModifier(Attributes.ATTACK_DAMAGE, GensokyoLegacy.loc("youkaified_atk"), AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
				lv -> readConfig(GLModConfig.SERVER.youkaifiedAttackBonus, 0.5));
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, GensokyoLegacy.loc("youkaified_spd"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
				lv -> readConfig(GLModConfig.SERVER.youkaifiedSpeedBonus, 0.3));
	}

	private static double readConfig(net.neoforged.neoforge.common.ModConfigSpec.DoubleValue value, double fallback) {
		try {
			return value.get();
		} catch (IllegalStateException e) {
			// Config not yet loaded (e.g. during effect registration). Real value is read later when the modifier is actually applied.
			return fallback;
		}
	}

	@Override
	public boolean applyEffectTick(LivingEntity e, int lv) {
		if (e instanceof Player player) {
			player.causeFoodExhaustion(0.02f);
			if (player.level().isClientSide()) return true;
			if (player.getFoodData().getFoodLevel() < 10) {
				e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 0, true, true));
			}
			if (player.getFoodData().getFoodLevel() < 6) {
				player.removeEffect(YHEffects.YOUKAIFIED);
			}
		}
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
		// not curable by milk
	}

}
