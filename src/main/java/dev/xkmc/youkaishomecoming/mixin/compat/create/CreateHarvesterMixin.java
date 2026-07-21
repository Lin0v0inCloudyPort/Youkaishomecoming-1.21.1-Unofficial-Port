package dev.xkmc.youkaishomecoming.mixin.compat.create;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import dev.xkmc.youkaishomecoming.compat.create.CreateHarvesterCompat;
import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.simibubi.create.content.contraptions.actors.harvester.HarvesterMovementBehaviour")
public abstract class CreateHarvesterMixin implements MovementBehaviour {

	@Inject(method = "visitNewPosition", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
	private void youkaishomecoming$handleCustomCrops(MovementContext context, BlockPos pos, CallbackInfo ci) {
		Level world = context.world;
		if (world.isClientSide) return;

		BlockState state = world.getBlockState(pos);
		if (!CreateHarvesterCompat.isGensokyoCrop(state)) return;
		if (!CreateHarvesterCompat.canHarvest(world, pos, state)) {
			ci.cancel();
			return;
		}

		GensokyoLegacy.LOGGER.debug("[YHC-Harvester] Harvesting {} at {}", state.getBlock(), pos);
		CreateHarvesterCompat.harvest(world, pos, state, stack -> collectOrDropItem(context, stack));
		ci.cancel();
	}
}
