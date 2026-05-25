package dev.xkmc.youkaishomecoming.mixin;

import dev.xkmc.danmakuapi.content.item.SpellItem;
import dev.xkmc.danmakuapi.content.spell.item.ItemSpell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(SpellItem.class)
public interface SpellItemAccessor {

	@Accessor("spell")
	Supplier<ItemSpell> getSpell();

}
