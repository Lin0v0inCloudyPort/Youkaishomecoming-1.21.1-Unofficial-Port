package dev.xkmc.youkaishomecoming.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.danmakuapi.content.entity.ItemBulletEntity;
import dev.xkmc.danmakuapi.content.entity.ItemLaserEntity;
import dev.xkmc.danmakuapi.content.spell.item.PlayerHolder;
import dev.xkmc.youkaishomecoming.compat.touhoulittlemaid.MaidDanmakuTask;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerHolder.class)
public class PlayerHolderMixin {

	@WrapOperation(
			at = @At(value = "INVOKE", target = "Ldev/xkmc/danmakuapi/content/entity/ItemBulletEntity;setup(FIZZLnet/minecraft/world/phys/Vec3;)V"),
			method = "prepareDanmaku")
	private void youkaishomecoming$buffMaidDanmaku(ItemBulletEntity self, float damage, int life, boolean bypassWall, boolean bypassEntity, Vec3 vec, Operation<Void> original) {
		LivingEntity owner = ((PlayerHolder) (Object) this).player();
		if (owner instanceof EntityMaid maid) {
			damage = damage * MaidDanmakuTask.getFavorabilityMultiplier(maid)
					+ (float) maid.getAttributeValue(Attributes.ATTACK_DAMAGE);
		}
		original.call(self, damage, life, bypassWall, bypassEntity, vec);
	}

	@WrapOperation(
			at = @At(value = "INVOKE", target = "Ldev/xkmc/danmakuapi/content/entity/ItemLaserEntity;setup(FIFZLnet/minecraft/world/phys/Vec3;)V"),
			method = "prepareLaser")
	private void youkaishomecoming$buffMaidLaser(ItemLaserEntity self, float damage, int life, float length, boolean bypassWall, Vec3 vec, Operation<Void> original) {
		LivingEntity owner = ((PlayerHolder) (Object) this).player();
		if (owner instanceof EntityMaid maid) {
			damage = damage * MaidDanmakuTask.getFavorabilityMultiplier(maid)
					+ (float) maid.getAttributeValue(Attributes.ATTACK_DAMAGE);
		}
		original.call(self, damage, life, length, bypassWall, vec);
	}

}
