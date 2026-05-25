package dev.xkmc.youkaishomecoming.compat.touhoulittlemaid;

import dev.xkmc.danmakuapi.api.DanmakuBullet;
import dev.xkmc.danmakuapi.api.DanmakuLaser;
import dev.xkmc.danmakuapi.content.entity.ItemBulletEntity;
import dev.xkmc.danmakuapi.content.entity.ItemLaserEntity;
import dev.xkmc.danmakuapi.content.spell.item.ItemSpell;
import dev.xkmc.danmakuapi.content.spell.spellcard.CardHolder;
import dev.xkmc.danmakuapi.init.registrate.DanmakuEntities;
import dev.xkmc.fastprojectileapi.collision.EntityStorageHelper;
import dev.xkmc.fastprojectileapi.entity.SimplifiedProjectile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record MaidSpellHolder(
        LivingEntity maid, Vec3 dir, ItemSpell spell, @Nullable LivingEntity targeted
) implements CardHolder {

    @Override
    public LivingEntity self() {
        return maid;
    }

    @Override
    public Vec3 center() {
        return maid.position().add(0, maid.getBbHeight() / 2, 0);
    }

    @Override
    public Vec3 forward() {
        var t = target();
        if (t == null) return dir;
        return t.subtract(center()).normalize();
    }

    @Override
    public @Nullable Vec3 target() {
        return spell.targetPos;
    }

    @Override
    public @Nullable Vec3 targetVelocity() {
        if (targeted == null) return null;
        return targeted.getDeltaMovement();
    }

    @Override
    public RandomSource random() {
        return maid.getRandom();
    }

    @Override
    public ItemBulletEntity prepareDanmaku(int life, Vec3 vec, DanmakuBullet type, DyeColor color) {
        ItemBulletEntity danmaku = new ItemBulletEntity(DanmakuEntities.ITEM_DANMAKU.get(), maid, maid.level());
        danmaku.setItem(type.get(color).asStack());
        danmaku.setup(type.damage(), life, true, true, vec);
        danmaku.setPos(center());
        return danmaku;
    }

    @Override
    public ItemLaserEntity prepareLaser(int life, Vec3 pos, Vec3 vec, float len, DanmakuLaser type, DyeColor color) {
        ItemLaserEntity danmaku = new ItemLaserEntity(DanmakuEntities.ITEM_LASER.get(), maid, maid.level());
        danmaku.setItem(type.get(color).asStack());
        danmaku.setup(type.damage(), life, len, true, vec);
        danmaku.setPos(pos);
        return danmaku;
    }

    @Override
    public void shoot(SimplifiedProjectile danmaku) {
        if (danmaku instanceof ItemBulletEntity e && e.afterExpiry != null) {
            e.afterExpiry.setup(this);
        }
        if (maid.level() instanceof ServerLevel sl) {
            EntityStorageHelper.fastAdd(sl, danmaku);
        }
    }

}
