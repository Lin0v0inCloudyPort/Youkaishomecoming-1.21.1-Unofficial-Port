package dev.xkmc.youkaishomecoming.mixin;

import dev.xkmc.youkaishomecoming.init.registrate.GLEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> effect);

	@Unique
	@Nullable
	private static Holder<MobEffect> enjoyableHolder;

	@Unique
	private static boolean enjoyableLookedUp;

	@Unique
	@Nullable
	private static Holder<MobEffect> getEnjoyable() {
		if (!enjoyableLookedUp) {
			enjoyableLookedUp = true;
			enjoyableHolder = BuiltInRegistries.MOB_EFFECT
					.getHolder(ResourceLocation.fromNamespaceAndPath("youkaishomecoming", "enjoyable"))
					.orElse(null);
		}
		return enjoyableHolder;
	}

	@Inject(at = @At("HEAD"), method = "canBeSeenAsEnemy", cancellable = true)
	public void youkaishomecoming$canBeSeenAsEnemy$unconscious(CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self instanceof Player player && hasEffect(GLEffects.UNCONSCIOUS)) {
			cir.setReturnValue(false);
		}
	}

	@Redirect(
			method = "addEatEffect",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z")
	)
	public boolean youkaishomecoming$addEatEffect$enjoyable(LivingEntity self, MobEffectInstance ins) {
		Holder<MobEffect> enjoyable = getEnjoyable();
		if (enjoyable != null) {
			MobEffectInstance enjoy = self.getEffect(enjoyable);
			if (enjoy != null && ins.getEffect().value().isBeneficial()) {
				int lv = enjoy.getAmplifier() + 1;
				int newDuration = (int) (ins.getDuration() * (1 + 0.2 * lv));
				ins = new MobEffectInstance(
						ins.getEffect(),
						newDuration,
						ins.getAmplifier(),
						ins.isAmbient(),
						ins.isVisible(),
						ins.showIcon()
				);
			}
		}
		return self.addEffect(ins);
	}

}
