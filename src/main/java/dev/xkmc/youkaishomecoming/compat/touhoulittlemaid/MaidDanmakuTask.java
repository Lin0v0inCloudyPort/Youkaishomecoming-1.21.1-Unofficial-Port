package dev.xkmc.youkaishomecoming.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.task.IAttackTask;
import com.github.tartaricacid.touhoulittlemaid.api.task.IRangedAttackTask;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidAttackStrafingTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.danmakuapi.content.entity.ItemBulletEntity;
import dev.xkmc.danmakuapi.content.item.DanmakuItem;
import dev.xkmc.danmakuapi.content.item.SpellItem;
import dev.xkmc.danmakuapi.init.registrate.DanmakuEntities;
import dev.xkmc.danmakuapi.init.registrate.DanmakuItems;
import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MaidDanmakuTask implements IRangedAttackTask {

    private static final ResourceLocation UID = GensokyoLegacy.loc("danmaku_attack");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public ItemStack getIcon() {
        return DanmakuItems.Bullet.CIRCLE.get(DyeColor.CYAN).asStack();
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound(EntityMaid maid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        BehaviorControl<EntityMaid> startTask = StartAttacking.create(this::canUseDanmaku, e -> findTarget(e));
        BehaviorControl<EntityMaid> stopTask = StopAttackingIfTargetInvalid.create(target -> !canUseDanmaku(maid) || farAway(target, maid));
        BehaviorControl<? super EntityMaid> moveTask = SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(0.6f);
        BehaviorControl<EntityMaid> strafeTask = new MaidAttackStrafingTask();
        BehaviorControl<EntityMaid> shootTask = new MaidDanmakuShootTask();
        return Lists.newArrayList(
                Pair.of(5, startTask),
                Pair.of(5, stopTask),
                Pair.of(5, moveTask),
                Pair.of(5, strafeTask),
                Pair.of(5, shootTask)
        );
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createRideBrainTasks(EntityMaid maid) {
        BehaviorControl<EntityMaid> startTask = StartAttacking.create(this::canUseDanmaku, e -> findTarget(e));
        BehaviorControl<EntityMaid> stopTask = StopAttackingIfTargetInvalid.create(target -> !canUseDanmaku(maid) || farAway(target, maid));
        BehaviorControl<EntityMaid> shootTask = new MaidDanmakuShootTask();
        return Lists.newArrayList(
                Pair.of(5, startTask),
                Pair.of(5, stopTask),
                Pair.of(5, shootTask)
        );
    }

    private Optional<? extends LivingEntity> findTarget(EntityMaid maid) {
        return maid.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .flatMap(mobs -> mobs.findClosest(e -> maid.canAttack(e) && maid.distanceTo(e) <= 64));
    }

    @Override
    public void performRangedAttack(EntityMaid shooter, LivingEntity target, float distanceFactor) {
        ItemStack stack = shooter.getMainHandItem();
        ItemStack bulletStack;
        if (stack.getItem() instanceof SpellItem spell) {
            // 符卡：用其对应的弹药类型作为弹幕外观
            var inv = shooter.getMaidInv();
            bulletStack = ItemStack.EMPTY;
            for (int i = 0; i < inv.getSlots(); i++) {
                if (spell.getAllSupportedProjectiles().test(inv.getStackInSlot(i))) {
                    bulletStack = inv.getStackInSlot(i).copy();
                    break;
                }
            }
            if (bulletStack.isEmpty()) bulletStack = stack.copy();
        } else if (stack.getItem() instanceof DanmakuItem) {
            bulletStack = stack.copy();
        } else {
            return;
        }

        double dx = target.getX() - shooter.getX();
        double dy = target.getY(0.5) - shooter.getEyeY();
        double dz = target.getZ() - shooter.getZ();
        Vec3 vec = new Vec3(dx, dy, dz).normalize().scale(0.8);

        ItemBulletEntity bullet = new ItemBulletEntity(DanmakuEntities.ITEM_DANMAKU.get(), shooter, shooter.level());
        bullet.setItem(bulletStack);
        float dmg = (float) shooter.getAttributeValue(Attributes.ATTACK_DAMAGE);
        dmg *= getFavorabilityMultiplier(shooter);
        bullet.setup(dmg, 60, true, true, vec);
        shooter.level().addFreshEntity(bullet);
    }

    public static int getFavorabilityMultiplier(EntityMaid maid) {
        int level = maid.getFavorabilityManager().getLevel();
        return switch (level) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 4;
            default -> 1;
        };
    }

    private boolean canUseDanmaku(EntityMaid maid) {
        if (isDanmakuOrSpell(maid.getMainHandItem())) return true;
        var inv = maid.getMaidInv();
        for (int i = 0; i < inv.getSlots(); i++) {
            if (isDanmakuOrSpell(inv.getStackInSlot(i))) return true;
        }
        // 也检查手持物品栏（handsInvWrapper 可能和 getMaidInv 不同）
        for (var hand : net.minecraft.world.InteractionHand.values()) {
            if (isDanmakuOrSpell(maid.getItemInHand(hand))) return true;
        }
        return false;
    }

    private static boolean isDanmakuOrSpell(ItemStack stack) {
        return stack.getItem() instanceof DanmakuItem || stack.getItem() instanceof SpellItem;
    }

    private boolean farAway(LivingEntity target, EntityMaid maid) {
        return maid.distanceTo(target) > 64;
    }

}
