package dev.xkmc.youkaishomecoming.content.spell.item;

import dev.xkmc.danmakuapi.content.entity.DanmakuHelper;
import dev.xkmc.danmakuapi.content.spell.item.ItemSpell;
import dev.xkmc.danmakuapi.content.spell.spellcard.CardHolder;
import dev.xkmc.danmakuapi.content.spell.spellcard.Ticker;
import dev.xkmc.danmakuapi.content.spell.spellcard.TrailAction;
import dev.xkmc.danmakuapi.init.registrate.DanmakuItems;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@SerialClass
public class ClownItemSpell extends ItemSpell {

	private static final int DUR = 60;

	@SerialField
	private int tick = 0;

	@Override
	public void start(LivingEntity entity, @Nullable LivingEntity target) {
		super.start(entity, target);
	}

	@Override
	public boolean tick(LivingEntity entity) {
		var rand = entity.getRandom();
		if (tick == 0 || tick == DUR) {
			int kind = tick / DUR % 2;
			if (kind == 0) {
				var sign = rand.nextBoolean() ? 1 : -1;
				var nor = DanmakuHelper.getOrientation(dir).asNormal();
				var ver = rand.nextDouble() * 20;
				for (int i = 0; i < 3; i++) {
					var n0 = nor.rotateDegrees(ver * sign);
					var n1 = nor.rotateDegrees(-ver * sign);
					addTicker(new Laser().init(dir, n0, 20, 1, kind).setTime(i * -10));
					addTicker(new Laser().init(dir, n1, 20, -1, kind).setTime(i * -10));
					ver += 30;
				}
			} else {
				var ver = (rand.nextDouble() * 2 - 1) * 60;
				var nor = DanmakuHelper.getOrientation(dir).asNormal();
				var n0 = nor.rotateDegrees(ver);
				var n1 = nor.rotateDegrees(-ver);
				addTicker(new Laser().init(dir, n0, 20, 1, kind));
				addTicker(new Laser().init(dir, n1, 20, -1, kind));
			}
		}
		if (tick % 10 == 0) {
			int kind = tick / DUR % 2;
			int round = tick / 10;
			if (tick % DUR / 10 < 4) {
				var col = kind == 0 ? DyeColor.RED : DyeColor.BLUE;
				var type = kind == 0 ? DanmakuItems.Bullet.STAR : DanmakuItems.Bullet.SPARK;
				addTicker(new Spread().init(col, type, dir,
						round % 2 == 0 ? 9 : -9, 0.8, 10));
			}
		}
		tick++;
		return tick > DUR & super.tick(entity);
	}

	@SerialClass
	public static class Laser extends Ticker<ClownItemSpell> {

		@SerialField
		private Vec3 dir = new Vec3(1, 0, 0);
		@SerialField
		private Vec3 nor = new Vec3(0, 1, 0);
		@SerialField
		private int dur, s, type;

		public Laser init(Vec3 dir, Vec3 nor, int dur, int s, int type) {
			this.dir = dir;
			this.nor = nor;
			this.dur = dur;
			this.s = s;
			this.type = type;
			return this;
		}

		public Laser setTime(int time) {
			this.tick = time;
			return this;
		}

		@Override
		public boolean tick(CardHolder holder, ClownItemSpell card) {
			super.tick(holder, card);

			if (tick == 0) {
				dir = holder.forward();
			}
			if (tick > 0) {
				double angle = (45 + (1d * tick / dur) * 180) * s;
				double forward = (-45 + (1d * tick / dur) * 90) * s;
				double ver = (-15 + (1d * tick / dur) * 30) * s;
				var o = DanmakuHelper.getOrientation(dir, nor);
				var ddir = o.rotateDegrees(angle);
				var col = type == 0 ? DyeColor.BLUE : DyeColor.RED;
				var e = holder.prepareDanmaku(5 + tick * 2, ddir.scale(0.5), DanmakuItems.Bullet.MENTOS, col);
				if (type == 0) {
					e.afterExpiry = new SpreadTrail().init(o.rotateDegrees(forward, ver));
				} else {
					e.afterExpiry = new HomingTrail();
				}
				holder.shoot(e);
			}
			return tick > dur;
		}

	}

	@SerialClass
	public static class Spread extends Ticker<ClownItemSpell> {

		@SerialField
		private DyeColor color = DyeColor.RED;

		@SerialField
		private DanmakuItems.Bullet type = DanmakuItems.Bullet.BALL;

		@SerialField
		private double w = 3, v = 0.5;

		@SerialField
		private int duration;

		@SerialField
		private Vec3 dir = new Vec3(1, 0, 0);

		public Spread init(DyeColor color, DanmakuItems.Bullet type, Vec3 dir, double w, double v, int duration) {
			this.color = color;
			this.type = type;
			this.dir = dir;
			this.w = w;
			this.v = v;
			this.duration = duration;
			return this;
		}

		@Override
		public boolean tick(CardHolder holder, ClownItemSpell card) {
			var r = holder.random();
			var o = DanmakuHelper.getOrientation(dir);
			int n = 3;
			var cen = holder.center();
			for (int i = 0; i < n; i++) {
				for (int j = -2; j <= 2; j++) {
					double hor = (tick - duration / 2d + 1d * i / n + r.nextDouble() / n) * w;
					double ver = r.nextInt(-3, 3) + j * 15;
					var ddir = o.rotateDegrees(hor, ver);
					var vel = ddir.scale(v);
					int life = (int) (40 / v * (1 + r.nextDouble() * 0.5));
					var e = holder.prepareDanmaku(life, vel, type, color);
					e.setPos(cen.add(vel.scale(1d * i / n)));
					holder.shoot(e);
				}
			}
			super.tick(holder, card);
			return tick >= duration;
		}
	}

	@SerialClass
	public static class SpreadTrail extends TrailAction {

		@SerialField
		private Vec3 forward = new Vec3(1, 0, 0);

		public SpreadTrail init(Vec3 dir) {
			this.forward = dir;
			return this;
		}

		@Override
		public void execute(CardHolder holder, Vec3 pos, Vec3 dir) {
			var e = holder.prepareLaser(DUR, pos, forward, 60, DanmakuItems.Laser.LASER, DyeColor.BLUE);
			e.setupTime(10, 10, DUR, 10);
			holder.shoot(e);
			var b = holder.prepareDanmaku(DUR + 20, Vec3.ZERO, DanmakuItems.Bullet.MENTOS, DyeColor.BLUE);
			b.setPos(pos);
			holder.shoot(b);
		}

	}

	@SerialClass
	public static class HomingTrail extends TrailAction {

		@Override
		public void execute(CardHolder holder, Vec3 pos, Vec3 dir) {
			var target = holder.target();
			if (target == null) return;
			var forward = target.subtract(pos).normalize();
			var e = holder.prepareLaser(DUR, pos, forward, 60, DanmakuItems.Laser.LASER, DyeColor.RED);
			e.setupTime(10, 10, DUR, 10);
			holder.shoot(e);
			var b = holder.prepareDanmaku(DUR + 20, Vec3.ZERO, DanmakuItems.Bullet.MENTOS, DyeColor.RED);
			b.setPos(pos);
			holder.shoot(b);
		}

	}

}
