package dev.xkmc.youkaishomecoming.content.entity.characters.boss;

import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.youkaishomecoming.content.entity.youkai.BossYoukaiEntity;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@SerialClass
public class RemiliaEntity extends BossYoukaiEntity {

	public RemiliaEntity(EntityType<? extends BossYoukaiEntity> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Override
	public boolean shouldIgnore(LivingEntity e) {
		return super.shouldIgnore(e) || !(e instanceof Player) &&
				(e.getType().is(EntityTypeTags.UNDEAD) || e.getType().is(EntityTypeTags.ILLAGER));
	}

}
