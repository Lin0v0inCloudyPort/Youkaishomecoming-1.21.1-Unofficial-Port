package dev.xkmc.youkaishomecoming.content.block.plant.grape;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.xkmc.youkaishomecoming.content.block.plant.rope.RopeLoggedCropBlock;
import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import dev.xkmc.youkaishomecoming.init.food.YHCrops;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrapeVineSet {

	private static final int FRUIT_CHANCE = 5;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 9);
	public static final IntegerProperty FRUIT_AGE = IntegerProperty.create("age", 5, 9);

	public final YHCrops crop;
	public final BlockEntry<GrapeTrunk> trunk;
	public final BlockEntry<GrapeVine> center;
	public final BlockEntry<GrapeBranch> side;
	public final BlockEntry<GrapeFruit> hanging;

// PLACEHOLDER_CONSTRUCTOR

	public GrapeVineSet(YHCrops crop) {
		this.crop = crop;
		String name = crop.getName();
		var reg = GensokyoLegacy.REGISTRATE;

		this.trunk = reg.<GrapeTrunk>block(name + "_trunk", p -> new GrapeTrunk(p, crop::getSeed))
				.properties(p -> BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
						.noCollission().randomTicks().strength(1)
						.sound(SoundType.CROP).pushReaction(PushReaction.DESTROY))
				.blockstate(this::buildTrunkModel)
				.loot(this::buildTrunkLoot)
				.tag(BlockTags.CLIMBABLE)
				.register();
		this.center = reg.<GrapeVine>block(name + "_vines", GrapeVine::new)
				.initialProperties(() -> Blocks.WHEAT)
				.blockstate(this::buildVineModel)
				.loot(this::buildVineLoot)
				.tag(BlockTags.CLIMBABLE)
				.register();
		this.side = reg.<GrapeBranch>block(name + "_branches", GrapeBranch::new)
				.initialProperties(() -> Blocks.WHEAT)
				.blockstate(this::buildBranchModel)
				.loot((pvd, block) -> buildVineLoot(pvd, block))
				.tag(BlockTags.CLIMBABLE)
				.register();
		this.hanging = reg.<GrapeFruit>block(name + "_fruits", GrapeFruit::new)
				.properties(p -> BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
						.noCollission().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY))
				.blockstate(this::buildFruitModel)
				.loot(this::buildFruitLoot)
				.register();
	}

// PLACEHOLDER_INNER_CLASSES

	public class GrapeTrunk extends VineTrunkBlock {
		public GrapeTrunk(Properties prop, ItemLike seed) {
			super(prop, seed);
		}

		@Override
		protected CenterCropVineBlock getTop() {
			return center.get();
		}
	}

	public class GrapeVine extends CenterCropVineBlock {
		public GrapeVine(Properties prop) {
			super(prop);
		}

		@Override
		protected VineTrunkBlock getTrunk() { return trunk.get(); }

		@Override
		protected BranchCropVineBlock getSide() { return side.get(); }

		@Override
		protected ItemLike getFruit() { return crop.getFruits(); }

		@Override
		public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
			return crop.getSeed().getDefaultInstance();
		}

		@Override
		protected IntegerProperty getAgeProperty() { return AGE; }

		@Override
		public int getMaxAge() { return 9; }

		@Override
		protected int getBaseAge() { return 6; }
	}

	public class GrapeBranch extends BranchCropVineBlock {
		public GrapeBranch(Properties prop) {
			super(prop);
		}

		@Override
		protected CenterCropVineBlock getCenter() { return center.get(); }

// PLACEHOLDER_BRANCH_REST

		@Override
		protected VineFruitBlock getHanging() { return hanging.get(); }

		@Override
		public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
			return crop.getSeed().getDefaultInstance();
		}

		@Override
		protected ItemLike getFruit() { return crop.getFruits(); }

		@Override
		protected IntegerProperty getAgeProperty() { return AGE; }

		@Override
		public int getMaxAge() { return 9; }

		@Override
		protected int getBaseAge() { return 6; }

		@Override
		protected int getFruitChance() { return FRUIT_CHANCE; }
	}

	public class GrapeFruit extends VineFruitBlock {
		public GrapeFruit(Properties prop) {
			super(prop);
			registerDefaultState(defaultBlockState().setValue(FRUIT_AGE, 5));
		}

		@Override
		protected ItemLike getFruit(BlockState state) { return crop.getFruits(); }

		@Override
		protected BranchCropVineBlock getHanger() { return side.get(); }

		@Override
		protected IntegerProperty getAgeProperty() { return FRUIT_AGE; }

		@Override
		public int getMaxAge() { return 9; }

		@Override
		protected int getBaseAge() { return 6; }
	}

// PLACEHOLDER_METHODS

	// --- Model generation ---

	private void buildTrunkModel(DataGenContext<Block, GrapeTrunk> ctx, RegistrateBlockstateProvider pvd) {
		String name = crop.getName();
		String type = getTypeName();
		pvd.getVariantBuilder(ctx.get()).forAllStates(state -> {
			boolean merged = state.getValue(VineTrunkBlock.MERGED);
			if (merged) {
				return ConfiguredModel.builder().modelFile(pvd.models().getBuilder("tree_" + name + "_trunk")
						.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/plant/grape_trunk")))
						.texture("cross", "block/plants/" + type + "/trunk_cross")
						.texture("post", "block/plants/" + type + "/trunk_post")
						.renderType("cutout")
				).build();
			} else {
				return ConfiguredModel.builder().modelFile(pvd.models().getBuilder("tree_" + name + "_trunk_rope")
						.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/rope_crop")))
						.texture("cross", "block/plants/" + type + "/trunk_cross")
						.texture("rope_side", pvd.modLoc("block/plants/rope"))
						.texture("rope_top", pvd.modLoc("block/plants/rope_top"))
						.renderType("cutout")
				).build();
			}
		});
	}

	private void buildVineModel(DataGenContext<Block, GrapeVine> ctx, RegistrateBlockstateProvider pvd) {
		String name = crop.getName();
		String[] strs = name.split("_");
		String col = strs[0];
		String type = strs[1];
		pvd.getVariantBuilder(ctx.get()).forAllStates(state -> ConfiguredModel.builder().modelFile(
				getVineModelFile(state, name, type, col, "vine", pvd, 9)
		).rotationY(state.getValue(CenterCropVineBlock.AXIS) == Direction.Axis.X ? 0 : 90).build());
	}

// PLACEHOLDER_METHODS2

	private void buildBranchModel(DataGenContext<Block, GrapeBranch> ctx, RegistrateBlockstateProvider pvd) {
		String name = crop.getName();
		String[] strs = name.split("_");
		String col = strs[0];
		String type = strs[1];
		pvd.horizontalBlock(ctx.get(), state -> getVineModelFile(state, name, type, col, "branch", pvd, 9), -90);
	}

	private void buildFruitModel(DataGenContext<Block, GrapeFruit> ctx, RegistrateBlockstateProvider pvd) {
		String name = crop.getName();
		String[] strs = name.split("_");
		String col = strs[0];
		String type = strs[1];
		pvd.horizontalBlock(ctx.get(), state -> {
			int age = state.getValue(FRUIT_AGE);
			String suffix = age == 9 ? "_" + col : "";
			String fruit = "block/plants/" + type + "/fruit/fruit" + age + suffix;
			String bottom = "block/plants/" + type + "/branch/bottom" + age + suffix;
			return pvd.models().getBuilder("tree_" + name + "_fruit" + age)
					.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/plant/grape_fruit")))
					.texture("fruit", pvd.modLoc(fruit))
					.texture("bottom", pvd.modLoc(bottom))
					.renderType("cutout");
		}, -90);
	}

	private BlockModelBuilder getVineModelFile(BlockState state, String name, String type, String col, String folder, RegistrateBlockstateProvider pvd, int maxAge) {
		int age = state.getValue(AGE);
		boolean top = state.getValue(BaseCropVineBlock.TOP);
		String suffix = age == maxAge ? "_" + col : "";
		String set = top ? "top" : folder;
		String coil = "block/plants/" + type + "/" + set + "/coil" + age + suffix;
		String vine = "block/plants/" + type + "/" + set + "/vine" + age + suffix;
		String leaves = "block/plants/" + type + "/leaves/leaves" + age + suffix;
		String model = "tree_" + name + "_" + folder + age;
		BlockModelBuilder builder;
		if (folder.equals("branch")) {
			boolean ext = state.getValue(BranchCropVineBlock.EXTENDED);
			builder = pvd.models().getBuilder(model + (top ? "_top" : "") + (ext ? "_extended" : ""));
			builder.texture("vine", pvd.modLoc(ext ? coil : vine));
		} else {
			boolean l = state.getValue(CenterCropVineBlock.LEFT);
			boolean r = state.getValue(CenterCropVineBlock.RIGHT);
			builder = pvd.models().getBuilder(model + (l ? "l" : "") + (r ? "r" : "") + (top ? "_top" : ""));
			builder.texture("coil_left", pvd.modLoc(l ? coil : vine))
					.texture("coil_right", pvd.modLoc(r ? coil : vine));
		}
		if (top) {
			builder.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/plant/grape_" + folder + "_top")));
			builder.texture("leaves", pvd.modLoc(leaves));
		} else if (age >= 5) {
			String bottom = "block/plants/" + type + "/" + folder + "/bottom" + age + suffix;
			builder.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/plant/grape_" + folder + "_mature")));
			builder.texture("bottom", pvd.modLoc(bottom));
			builder.texture("leaves", pvd.modLoc(leaves));
		} else {
			builder.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/plant/grape_" + folder)));
		}
		builder.texture("coil", pvd.modLoc(coil))
				.texture("rope_top", pvd.modLoc("block/plants/rope_top"))
				.renderType("cutout");
		return builder;
	}

// PLACEHOLDER_LOOT

	// --- Loot tables ---

	private void buildTrunkLoot(RegistrateBlockLootTables pvd, GrapeTrunk block) {
		pvd.add(block, LootTable.lootTable().withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(block.seed))
				.add(LootItem.lootTableItem(Items.STICK))
		));
	}

	private void buildVineLoot(RegistrateBlockLootTables pvd, BaseCropVineBlock block) {
		pvd.add(block, pvd.applyExplosionDecay(block,
				LootTable.lootTable().withPool(LootPool.lootPool()
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
								.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(block.getAgeProperty(), 9)))
						.add(LootItem.lootTableItem(crop.getFruits())
								.apply(ApplyBonusCount.addBonusBinomialDistributionCount(
										pvd.getRegistries().holderOrThrow(Enchantments.FORTUNE), 0.5714286F, 3)))
				)));
	}

	private void buildFruitLoot(RegistrateBlockLootTables pvd, GrapeFruit block) {
		pvd.add(block, pvd.applyExplosionDecay(block,
				LootTable.lootTable().withPool(LootPool.lootPool()
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
								.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(block.getAgeProperty(), 9)))
						.add(LootItem.lootTableItem(crop.getFruits())
								.apply(ApplyBonusCount.addBonusBinomialDistributionCount(
										pvd.getRegistries().holderOrThrow(Enchantments.FORTUNE), 0.5714286F, 3)))
				)));
	}

	private String getTypeName() {
		String name = crop.getName();
		return name.split("_")[1];
	}

// PLACEHOLDER_STATIC

	// --- Static methods for GrapeCropBlock registration ---

	public static void buildPlantModel(DataGenContext<Block, GrapeCropBlock> ctx, RegistrateBlockstateProvider pvd, String name) {
		String[] strs = name.split("_");
		String col = strs[0];
		String type = strs[1];
		int start = ctx.get().getDoubleBlockStart();
		var empty = pvd.models()
				.withExistingParent("small_" + name + "_upper_empty", pvd.mcLoc("block/air"))
				.texture("particle", pvd.modLoc("block/plants/" + type + "/small/upper" + start));
		pvd.getVariantBuilder(ctx.get()).forAllStates(state -> {
			int age = state.getValue(GrapeCropBlock.AGE);
			boolean rope = state.getValue(RopeLoggedCropBlock.ROPELOGGED);
			boolean root = state.getValue(DoubleRopeCropBlock.ROOT);
			if (!root && age < start) {
				return ConfiguredModel.builder().modelFile(empty).build();
			}
			String tag = (root ? "lower" : "upper") + age;
			String tex = "block/plants/" + type + "/small/" + tag;
			if (age == ctx.get().getMaxAge()) {
				tex += "_" + col;
			}
			if (rope) {
				return ConfiguredModel.builder().modelFile(pvd.models().getBuilder("small_rope_" + name + "_" + tag)
						.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/rope_crop")))
						.texture("cross", tex)
						.texture("rope_side", pvd.modLoc("block/plants/rope"))
						.texture("rope_top", pvd.modLoc("block/plants/rope_top"))
						.renderType("cutout")
				).build();
			} else {
				return ConfiguredModel.builder().modelFile(pvd.models().getBuilder("small_bare_" + name + "_" + tag)
						.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/rope_crop_bare")))
						.texture("cross", tex)
						.renderType("cutout")
				).build();
			}
		});
	}

// PLACEHOLDER_STATIC2

	public static void buildPlantLoot(RegistrateBlockLootTables pvd, GrapeCropBlock block, YHCrops crop) {
		pvd.add(block, pvd.applyExplosionDecay(block,
				LootTable.lootTable().withPool(LootPool.lootPool()
								.add(LootItem.lootTableItem(crop.getSeed()))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(block.getAgeProperty(), block.getMaxAge())).invert())
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoubleRopeCropBlock.ROOT, true))))
						.withPool(LootPool.lootPool()
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(block.getAgeProperty(), block.getMaxAge())))
								.add(LootItem.lootTableItem(crop.getFruits())
										.apply(ApplyBonusCount.addBonusBinomialDistributionCount(
												pvd.getRegistries().holderOrThrow(Enchantments.FORTUNE), 0.5714286F, 3)))
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoubleRopeCropBlock.ROOT, true)))
						)));
	}

	public static void buildWildModel(DataGenContext<Block, ?> ctx, RegistrateBlockstateProvider pvd, YHCrops crop) {
		String name = crop.getName();
		String tex = "wild_" + name;
		pvd.simpleBlock(ctx.get(), pvd.models().cross(tex, pvd.modLoc("block/plants/grape/" + tex)).renderType("cutout"));
	}

}
