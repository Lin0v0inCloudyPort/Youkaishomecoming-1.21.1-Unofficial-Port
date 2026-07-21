package dev.xkmc.youkaishomecoming.content.item.curio;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CamelliaItem extends Item {

	public CamelliaItem(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.HEAD;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
		list.add(Component.translatable("youkaishomecoming.tooltip.camellia").withStyle(net.minecraft.ChatFormatting.GRAY));
	}

}
