package dev.xkmc.youkaishomecoming.mixin;

import dev.xkmc.danmakuapi.content.spell.item.ItemSpell;
import dev.xkmc.danmakuapi.content.spell.spellcard.CardHolder;
import dev.xkmc.danmakuapi.content.spell.spellcard.Ticker;
import dev.xkmc.fastprojectileapi.entity.SimplifiedProjectile;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemSpell.class)
public interface ItemSpellAccessor {

	@Accessor("tickers")
	ArrayList<Ticker<?>> getTickers();

	@Accessor("holder")
	void setHolder(CardHolder holder);

	@Accessor("targetCache")
	void setTargetCache(LivingEntity target);

	@Accessor("cache")
	List<SimplifiedProjectile> getCache();

}
