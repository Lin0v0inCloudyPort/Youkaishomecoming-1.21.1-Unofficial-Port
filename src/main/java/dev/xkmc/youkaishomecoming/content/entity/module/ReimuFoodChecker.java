package dev.xkmc.youkaishomecoming.content.entity.module;

import com.google.common.collect.Streams;
import dev.xkmc.youkaishomecoming.content.pot.table.food.YHRolls;
import dev.xkmc.youkaishomecoming.content.pot.table.food.YHSushi;
import dev.xkmc.youkaishomecoming.init.food.YHBowl;
import dev.xkmc.youkaishomecoming.init.food.YHDish;
import dev.xkmc.youkaishomecoming.init.food.YHDrink;
import dev.xkmc.youkaishomecoming.init.food.YHFood;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ReimuFoodChecker {

	private static Set<Item> WHITELIST;

	private static final Set<String> EXCLUDE_IDS = Set.of(
			"youkaishomecoming:cooked_mandrake_root",
			"youkaishomecoming:crab",
			"youkaishomecoming:crab_meat",
			"youkaishomecoming:crab_roe",
			"youkaishomecoming:cucumber_slice",
			"youkaishomecoming:grilled_boarchop",
			"youkaishomecoming:grilled_boarchop_bits",
			"youkaishomecoming:grilled_venison",
			"youkaishomecoming:grilled_venison_slice",
			"youkaishomecoming:otoro",
			"youkaishomecoming:raw_boarchop",
			"youkaishomecoming:raw_boarchop_bits",
			"youkaishomecoming:raw_lamprey",
			"youkaishomecoming:raw_lamprey_fillet",
			"youkaishomecoming:raw_tuna",
			"youkaishomecoming:raw_tuna_slice",
			"youkaishomecoming:raw_venison",
			"youkaishomecoming:raw_venison_slice",
			"youkaishomecoming:roasted_lamprey",
			"youkaishomecoming:roasted_lamprey_fillet",
			"youkaishomecoming:roe",
			"youkaishomecoming:seared_tuna",
			"youkaishomecoming:seared_tuna_slice",
			"youkaishomecoming:steamed_crab",
			"youkaishomecoming:tamagoyaki_slice",
			"youkaishomecoming:tofu"
	);

	public static boolean isLoveFood(Item item) {
		if (WHITELIST == null) init();
		return WHITELIST.contains(item);
	}

	private static synchronized void init() {
		if (WHITELIST != null) return;
		Set<Item> set = new HashSet<>();
		Streams.concat(
				Arrays.stream(YHDish.values()).map(e -> e.block.get()),
				Arrays.stream(YHDrink.values()).filter(e -> !e.isFlesh()).map(e -> e.item.get()),
				Arrays.stream(YHFood.values()).filter(e -> e.type.isReimuFood()).map(e -> e.item.get()),
				Arrays.stream(YHBowl.values()).filter(YHBowl::isReimuFood).map(e -> e.item.get()),
				Arrays.stream(YHSushi.values()).filter(YHSushi::isReimuFood).map(e -> e.item.get()),
				Arrays.stream(YHRolls.values()).map(e -> e.slice.get())
		).distinct().forEach(e -> set.add(e.asItem()));
		set.removeIf(item -> EXCLUDE_IDS.contains(BuiltInRegistries.ITEM.getKey(item).toString()));
		WHITELIST = set;
	}

	private ReimuFoodChecker() {}
}
