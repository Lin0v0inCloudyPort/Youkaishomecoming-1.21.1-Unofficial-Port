package dev.xkmc.youkaishomecoming.event;

import dev.xkmc.youkaishomecoming.content.block.plant.DoubleCropBlock;
import dev.xkmc.youkaishomecoming.content.block.plant.rope.RopeLoggedCropBlock;
import dev.xkmc.youkaishomecoming.content.entity.characters.rumia.RumiaEntity;
import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = GensokyoLegacy.MODID)
public class GLGeneralEventHandlers {

	@SubscribeEvent
	public static void onShieldBlock(LivingShieldBlockEvent event) {
		if (event.getBlocked() && event.getDamageSource().getDirectEntity() instanceof RumiaEntity rumia) {
			rumia.state.onBlocked();
		}
	}

	/**
	 * Pre-empts other mods (e.g. Diversity) that subscribe to RightClickBlock at NORMAL priority
	 * and blanket-handle anything {@code instanceof CropBlock}. Our rope-logged crops need their
	 * own {@link RopeLoggedCropBlock#useItemOn} to run so the cucumber-top advancement triggers
	 * and the rope stays in place after harvest.
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRightClickRopeCrop(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof RopeLoggedCropBlock crop)) return;
		if (crop.getAge(state) != crop.getMaxAge()) return;

		ItemInteractionResult result = state.useItemOn(
				event.getItemStack(), level, event.getEntity(), event.getHand(), event.getHitVec());
		if (!result.consumesAction()) return;
		event.setCancellationResult(result.result());
		event.setCanceled(true);
	}

	/**
	 * Same problem as {@link #onRightClickRopeCrop}, but for {@link DoubleCropBlock} (coffea, tea).
	 * Without this, clicking the LOWER half of a mature two-tall crop is intercepted by other mods
	 * that treat any {@code CropBlock} as a generic crop, resetting AGE to 0 and removing the UPPER
	 * half via {@link DoubleCropBlock#setGrowth} since 0 is below {@code getDoubleBlockStart()}.
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onRightClickDoubleCrop(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof DoubleCropBlock)) return;

		if (event.getItemStack().isEmpty()) {
			InteractionResult r = state.useWithoutItem(level, event.getEntity(), event.getHitVec());
			if (!r.consumesAction()) return;
			event.setCancellationResult(r);
			event.setCanceled(true);
			return;
		}

		ItemInteractionResult result = state.useItemOn(
				event.getItemStack(), level, event.getEntity(), event.getHand(), event.getHitVec());
		if (!result.consumesAction()) return;
		event.setCancellationResult(result.result());
		event.setCanceled(true);
	}

}
