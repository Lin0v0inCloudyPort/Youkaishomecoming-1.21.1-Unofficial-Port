package dev.xkmc.youkaishomecoming.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xkmc.youkaishomecoming.content.item.character.TouhouHatItem;
import dev.xkmc.youkaishomecoming.init.data.GLModConfig;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class HatCurioRenderer implements ICurioRenderer {

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(
			ItemStack stack, SlotContext slotContext, PoseStack matrixStack,
			RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer,
			int light, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch) {
		if (!GLModConfig.SERVER.curiosSupportEnabled.get()) return;
		if (!(stack.getItem() instanceof TouhouHatItem hat)) return;
		LivingEntity wearer = slotContext.entity();

		HumanoidModel<?> baseModel = renderLayerParent.getModel() instanceof HumanoidModel<?> h
				? h : null;
		if (baseModel == null) return;

		HumanoidModel<?> hatModel = IClientItemExtensions.of(stack)
				.getHumanoidArmorModel(wearer, stack, EquipmentSlot.HEAD, (HumanoidModel) baseModel);
		if (hatModel == null) return;

		ResourceLocation texture = hat.getArmorTexture(stack, wearer, EquipmentSlot.HEAD,
				(ArmorMaterial.Layer) null, false);
		if (texture == null) return;

		ICurioRenderer.followBodyRotations(wearer, (HumanoidModel<LivingEntity>) hatModel);
		ICurioRenderer.followHeadRotations(wearer, hatModel.head, hatModel.hat);

		var vertex = ItemRenderer.getArmorFoilBuffer(buffer,
				RenderType.armorCutoutNoCull(texture), stack.hasFoil());
		hatModel.renderToBuffer(matrixStack, vertex, light, OverlayTexture.NO_OVERLAY);
	}
}
