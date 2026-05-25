package dev.xkmc.youkaishomecoming.content.block.deco;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.xkmc.l2modularblock.core.BlockTemplates;
import dev.xkmc.l2modularblock.core.DelegateBlock;
import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import dev.xkmc.youkaishomecoming.init.data.TagRef;
import dev.xkmc.youkaishomecoming.init.data.YHTagGen;
import dev.xkmc.youkaishomecoming.init.food.YHCrops;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.Lazy;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Supplier;

public enum Baskets {
	APPLE("apple", Items.APPLE),
	GOLDEN_APPLE("golden_apple", Items.GOLDEN_APPLE),
	CABBAGE("cabbage", () -> ModItems.CABBAGE.get(), TagRef.FOOD_CABBAGE),
	CARROT("carrot", Items.CARROT),
	GOLDEN_CARROT("golden_carrot", Items.GOLDEN_CARROT),
	CUCUMBER("cucumber", () -> YHCrops.CUCUMBER.getFruits(), YHTagGen.CUCUMBER_SLICE), // tag fallback unused; ingredient drives matching
	;

	final Lazy<Ingredient> test;
	final Supplier<? extends ItemLike> item;
	final BlockEntry<DelegateBlock> block;
	private final String id;

	Baskets(String id, ItemLike item) {
		this.id = id;
		this.item = () -> item;
		this.test = Lazy.of(() -> Ingredient.of(item));
		this.block = build();
	}

	Baskets(String id, Supplier<? extends ItemLike> itemSup, TagKey<Item> tag) {
		this.id = id;
		this.item = itemSup;
		this.test = Lazy.of(() -> Ingredient.of(tag));
		this.block = build();
	}

	private BlockEntry<DelegateBlock> build() {
		return GensokyoLegacy.REGISTRATE.block(id + "_basket", p -> DelegateBlock.newBaseBlock(p,
						BlockTemplates.HORIZONTAL, new BasketBlock.Filled(this)))
				.blockstate((ctx, pvd) -> BasketBlock.buildStackModel(ctx, pvd, id))
				.initialProperties(() -> Blocks.BAMBOO_SLAB)
				.simpleItem()
				.tag(BlockTags.MINEABLE_WITH_AXE)
				.loot((pvd, b) -> BasketBlock.loot(pvd, b, item.get()))
				.register();
	}

	public static void register() {
	}
}
