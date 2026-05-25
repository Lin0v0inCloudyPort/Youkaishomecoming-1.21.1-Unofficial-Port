package dev.xkmc.youkaishomecoming.content.block.deco;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.xkmc.l2core.serial.loot.LootTableTemplate;
import dev.xkmc.l2modularblock.core.BlockTemplates;
import dev.xkmc.l2modularblock.core.DelegateBlock;
import dev.xkmc.l2modularblock.mult.CreateBlockStateBlockMethod;
import dev.xkmc.l2modularblock.mult.DefaultStateBlockMethod;
import dev.xkmc.l2modularblock.mult.UseItemOnBlockMethod;
import dev.xkmc.l2modularblock.mult.UseWithoutItemBlockMethod;
import dev.xkmc.l2modularblock.one.ShapeBlockMethod;
import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import dev.xkmc.youkaishomecoming.init.data.GLRecipeGen;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import vectorwing.farmersdelight.common.registry.ModItems;

public class BasketBlock {

	public static final int MAX = 11;
	public static final IntegerProperty COUNT = IntegerProperty.create("count", 1, MAX);
	public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

	public static final BlockEntry<DelegateBlock> BASKET;

	static {
		BASKET = GensokyoLegacy.REGISTRATE.block("short_basket", p -> DelegateBlock.newBaseBlock(p,
						BlockTemplates.HORIZONTAL, new Empty()))
				.blockstate(BasketBlock::buildBasketModel)
				.initialProperties(() -> Blocks.BAMBOO_SLAB)
				.simpleItem()
				.tag(BlockTags.MINEABLE_WITH_AXE)
				.recipe((ctx, pvd) -> GLRecipeGen.unlock(pvd,
						ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ctx.get())::unlockedBy,
						Items.BAMBOO)
						.pattern("S S").pattern("PPP")
						.define('S', Items.BAMBOO)
						.define('P', ModItems.CANVAS.get())
						.save(pvd))
				.register();

		Baskets.register();
	}

	public record Empty() implements ShapeBlockMethod, UseItemOnBlockMethod {

		@Override
		public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
			return SHAPE;
		}

		@Override
		public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
			for (var e : Baskets.values()) {
				if (e.test.get().test(stack)) {
					if (!level.isClientSide()) {
						stack.shrink(1);
						var next = e.block.getDefaultState()
								.setValue(BlockTemplates.HORIZONTAL_FACING, state.getValue(BlockTemplates.HORIZONTAL_FACING))
								.setValue(COUNT, 1);
						level.setBlockAndUpdate(pos, next);
					}
					return ItemInteractionResult.sidedSuccess(level.isClientSide());
				}
			}
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

	}

	public record Filled(Baskets baskets) implements CreateBlockStateBlockMethod, DefaultStateBlockMethod,
			ShapeBlockMethod, UseItemOnBlockMethod, UseWithoutItemBlockMethod {

		@Override
		public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
			int current = state.getValue(COUNT);
			if (current < MAX && baskets.test.get().test(stack)) {
				if (!level.isClientSide()) {
					stack.shrink(1);
					var next = baskets.block.getDefaultState()
							.setValue(BlockTemplates.HORIZONTAL_FACING, state.getValue(BlockTemplates.HORIZONTAL_FACING))
							.setValue(COUNT, current + 1);
					level.setBlockAndUpdate(pos, next);
				}
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		@Override
		public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
			int current = state.getValue(COUNT);
			if (!level.isClientSide()) {
				player.getInventory().placeItemBackInInventory(baskets.item.get().asItem().getDefaultInstance());
				var next = current == 1 ? BASKET.getDefaultState()
						.setValue(BlockTemplates.HORIZONTAL_FACING, state.getValue(BlockTemplates.HORIZONTAL_FACING)) :
						state.setValue(COUNT, current - 1);
				level.setBlockAndUpdate(pos, next);
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}

		@Override
		public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(COUNT);
		}

		@Override
		public BlockState getDefaultState(BlockState state) {
			return state.setValue(COUNT, MAX);
		}

		@Override
		public @Nullable VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
			return SHAPE;
		}

	}

	public static void buildBasketModel(DataGenContext<Block, DelegateBlock> ctx, RegistrateBlockstateProvider pvd) {
		// Models / blockstates are hand-authored under
		// assets/youkaishomecoming/{models,blockstates}/short_basket.json
		// nothing to generate
	}

	public static void buildStackModel(DataGenContext<Block, DelegateBlock> ctx, RegistrateBlockstateProvider pvd, String id) {
		// Models / blockstates are hand-authored under
		// assets/youkaishomecoming/{models,blockstates}/<id>_basket.json
		// nothing to generate
	}

	public static void loot(RegistrateBlockLootTables pvd, DelegateBlock b, ItemLike item) {
		var loot = LootTable.lootTable();
		loot.withPool(LootPool.lootPool().add(LootItem.lootTableItem(b))
				.when(LootTableTemplate.withBlockState(b, COUNT, MAX)));
		loot.withPool(LootPool.lootPool().add(LootItem.lootTableItem(BASKET.get()))
				.when(LootTableTemplate.withBlockState(b, COUNT, MAX).invert()));
		for (int i = 1; i < MAX; i++)
			loot.withPool(LootPool.lootPool().add(LootItem.lootTableItem(item))
					.when(LootTableTemplate.withBlockState(b, COUNT, i)));
		pvd.add(b, loot);
	}

	public static void register() {
	}

}
