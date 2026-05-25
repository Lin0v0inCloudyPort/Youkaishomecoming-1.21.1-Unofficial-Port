package dev.xkmc.youkaishomecoming.content.entity.characters.maiden;

import dev.xkmc.danmakuapi.init.data.DanmakuDamageTypes;
import dev.xkmc.youkaishomecoming.compat.touhoulittlemaid.TouhouConditionalSpawns;
import dev.xkmc.youkaishomecoming.content.entity.module.AbstractYoukaiModule;
import dev.xkmc.youkaishomecoming.content.entity.module.FeedModule;
import dev.xkmc.youkaishomecoming.content.entity.module.HomeModule;
import dev.xkmc.youkaishomecoming.content.entity.module.TalkModule;
import dev.xkmc.youkaishomecoming.content.entity.youkai.YoukaiFeatureSet;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import java.util.List;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

@SerialClass
public class ReimuEntity extends MaidenEntity implements GeoEntity {
	protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

	public ReimuEntity(EntityType<? extends ReimuEntity> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Override
	public YoukaiFeatureSet getFeatures() {
		return YoukaiFeatureSet.MAIDEN;
	}

	@Override
	protected List<AbstractYoukaiModule> createModules() {
		return List.of(new HomeModule(this), new FeedModule(this), new TalkModule(this));
	}

	@Override
	protected void onKilledBy(LivingEntity le, DamageSource source) {
		super.onKilledBy(le, source);
		if (!source.is(DanmakuDamageTypes.DANMAKU_TYPE)) {
			TouhouConditionalSpawns.triggetYukari(le, position());
		}
	}

	protected <E extends ReimuEntity> PlayState idleAnimController(final AnimationState<E> event) {
		return event.setAndContinue(IDLE);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "Flying", 5, this::idleAnimController));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.geoCache;
	}
}
