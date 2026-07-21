package dev.xkmc.youkaishomecoming.compat.create;

import dev.xkmc.youkaishomecoming.content.block.plant.DoubleCropBlock;
import dev.xkmc.youkaishomecoming.content.block.plant.TeaCropBlock;
import dev.xkmc.youkaishomecoming.content.block.plant.grape.BaseCropVineBlock;
import dev.xkmc.youkaishomecoming.content.block.plant.grape.DoubleRopeCropBlock;
import dev.xkmc.youkaishomecoming.content.block.plant.grape.GrapeCropBlock;
import dev.xkmc.youkaishomecoming.content.block.plant.grape.VineFruitBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.function.Consumer;

public class CreateHarvesterCompat {

	public static boolean isGensokyoCrop(BlockState state) {
		Block block = state.getBlock();
		return block instanceof TeaCropBlock
				|| block instanceof GrapeCropBlock
				|| block instanceof BaseCropVineBlock
				|| block instanceof VineFruitBlock;
	}

	public static boolean canHarvest(Level level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		if (block instanceof TeaCropBlock tea) {
			return getTeaAge(tea, state, level, pos) >= tea.getDoubleBlockStart();
		}
		if (block instanceof GrapeCropBlock grape) {
			return getGrapeAge(grape, state, level, pos) >= grape.getBaseAge();
		}
		if (block instanceof BaseCropVineBlock vine) {
			return state.getValue(vine.getHarvesterAgeProperty()) >= vine.getMaxAge();
		}
		if (block instanceof VineFruitBlock fruit) {
			return state.getValue(fruit.getHarvesterAgeProperty()) >= fruit.getHarvesterMaxAge();
		}
		return false;
	}

	public static void harvest(Level level, BlockPos pos, BlockState state, Consumer<ItemStack> itemCollector) {
		Block block = state.getBlock();
		if (block instanceof TeaCropBlock tea) {
			harvestTea(tea, level, pos, state, itemCollector);
		} else if (block instanceof GrapeCropBlock grape) {
			harvestGrape(grape, level, pos, state, itemCollector);
		} else if (block instanceof BaseCropVineBlock vine) {
			harvestVine(vine, level, pos, state, itemCollector);
		} else if (block instanceof VineFruitBlock fruit) {
			harvestFruit(fruit, level, pos, state, itemCollector);
		}
	}

	private static void harvestTea(TeaCropBlock tea, Level level, BlockPos pos, BlockState state, Consumer<ItemStack> itemCollector) {
		BlockPos lowerPos = pos;
		if (state.getValue(DoubleCropBlock.HALF) == DoubleBlockHalf.UPPER) {
			lowerPos = pos.below();
			state = level.getBlockState(lowerPos);
			if (!(state.getBlock() instanceof TeaCropBlock)) return;
		}
		int age = state.getValue(TeaCropBlock.AGE);
		if (age < tea.getDoubleBlockStart()) return;

		ItemStack drop = tea.getHarvesterPickupResult(level, state);
		if (!drop.isEmpty()) {
			itemCollector.accept(drop);
		}

		int resetAge = tea.getHarvesterResetAge();
		level.setBlockAndUpdate(lowerPos, state.setValue(TeaCropBlock.AGE, resetAge));
		BlockPos upperPos = lowerPos.above();
		BlockState upperState = level.getBlockState(upperPos);
		if (upperState.is(tea) && upperState.getValue(DoubleCropBlock.HALF) == DoubleBlockHalf.UPPER) {
			level.setBlockAndUpdate(upperPos, Blocks.AIR.defaultBlockState());
		}
	}

	private static void harvestGrape(GrapeCropBlock grape, Level level, BlockPos pos, BlockState state, Consumer<ItemStack> itemCollector) {
		BlockPos lowerPos = pos;
		if (!state.getValue(DoubleRopeCropBlock.ROOT)) {
			lowerPos = pos.below();
			state = level.getBlockState(lowerPos);
			if (!(state.getBlock() instanceof GrapeCropBlock)) return;
		}
		int age = state.getValue(GrapeCropBlock.AGE);
		if (age < grape.getBaseAge()) return;

		int quantity = 1 + level.random.nextInt(2);
		itemCollector.accept(new ItemStack(grape.getFruitItem(), quantity));

		int resetAge = grape.getBaseAge();
		level.setBlockAndUpdate(lowerPos, state.setValue(GrapeCropBlock.AGE, resetAge));
		BlockPos upperPos = lowerPos.above();
		BlockState upperState = level.getBlockState(upperPos);
		if (upperState.is(grape)) {
			level.setBlockAndUpdate(upperPos, upperState.setValue(GrapeCropBlock.AGE, resetAge));
		}
	}

	private static void harvestVine(BaseCropVineBlock vine, Level level, BlockPos pos, BlockState state, Consumer<ItemStack> itemCollector) {
		int quantity = 1 + level.random.nextInt(2);
		itemCollector.accept(new ItemStack(vine.getHarvesterFruit(), quantity));
		level.setBlockAndUpdate(pos, state.setValue(vine.getHarvesterAgeProperty(), vine.getHarvesterBaseAge()));
	}

	private static void harvestFruit(VineFruitBlock fruit, Level level, BlockPos pos, BlockState state, Consumer<ItemStack> itemCollector) {
		int quantity = 1 + level.random.nextInt(2);
		itemCollector.accept(new ItemStack(fruit.getHarvesterFruit(state), quantity));
		level.setBlockAndUpdate(pos, state.setValue(fruit.getHarvesterAgeProperty(), fruit.getHarvesterBaseAge()));
	}

	private static int getTeaAge(TeaCropBlock tea, BlockState state, Level level, BlockPos pos) {
		if (state.getValue(DoubleCropBlock.HALF) == DoubleBlockHalf.UPPER) {
			state = level.getBlockState(pos.below());
			if (!(state.getBlock() instanceof TeaCropBlock)) return 0;
		}
		return state.getValue(TeaCropBlock.AGE);
	}

	private static int getGrapeAge(GrapeCropBlock grape, BlockState state, Level level, BlockPos pos) {
		if (!state.getValue(DoubleRopeCropBlock.ROOT)) {
			state = level.getBlockState(pos.below());
			if (!(state.getBlock() instanceof GrapeCropBlock)) return 0;
		}
		return state.getValue(GrapeCropBlock.AGE);
	}
}
