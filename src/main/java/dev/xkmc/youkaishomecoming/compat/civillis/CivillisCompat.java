package dev.xkmc.youkaishomecoming.compat.civillis;

import dev.xkmc.youkaishomecoming.content.entity.youkai.YoukaiEntity;
import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.List;

@EventBusSubscriber(modid = GensokyoLegacy.MODID)
public class CivillisCompat {

	private static final boolean CIVILLIS_LOADED = ModList.get().isLoaded("civil");

	@SubscribeEvent
	public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
		if (!CIVILLIS_LOADED) return;
		if (!(event.getEntity() instanceof YoukaiEntity youkai)) return;
		if (event.getLevel().isClientSide()) return;
		removeCivillisFleeGoals(youkai);
	}

	private static void removeCivillisFleeGoals(YoukaiEntity youkai) {
		List<WrappedGoal> goals = youkai.goalSelector.getAvailableGoals().stream().toList();
		for (WrappedGoal wrapped : goals) {
			Goal goal = wrapped.getGoal();
			if (goal.getClass().getName().contains("civil.mob.FleeCivilizationGoal")) {
				youkai.goalSelector.removeGoal(goal);
			}
		}
	}

}
