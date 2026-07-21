package dev.xkmc.youkaishomecoming.mixin.compat.ftbultimine;

import dev.xkmc.youkaishomecoming.content.block.plant.DoubleCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * FTB Ultimine's {@code VanillaCropLikeHandler} treats any {@code BushBlock}/{@code CropBlock}
 * as a generic crop and resets its age to 0 via {@code setBlock}, which corrupts our two-tall
 * coffea/tea: the LOWER half is reset but the UPPER half is left orphaned at age 11 and
 * eventually self-destructs via canSurvive.
 *
 * <p>Ultimine's {@code single_crop_harvesting_blacklist} tag only short-circuits the single-
 * block path; the multi-block ultimine path in {@code CropHarvesting} bypasses the tag.
 *
 * <p>We patch {@code VanillaCropLikeHandler.isApplicable} to opt out only our own
 * {@link DoubleCropBlock} instances, so other mods' crops (and any blocks they add to the
 * blacklist tag) keep their ultimine harvesting behavior untouched.
 *
 * <p>Uses {@link Pseudo} + string target so this mixin is silently skipped when FTB Ultimine
 * is not loaded.
 */
@Pseudo
@Mixin(targets = "dev.ftb.mods.ftbultimine.crops.VanillaCropLikeHandler", remap = false)
public class FTBUltimineCropHarvestingMixin {

	@Inject(method = "isApplicable(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
			at = @At("HEAD"), cancellable = true, remap = false)
	private void youkaishomecoming$skipDoubleCrop(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof DoubleCropBlock) {
			cir.setReturnValue(false);
		}
	}
}
