package com.teamwizardry.wizardry.common.entity.angel.zachriel;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;

public class Tardis {

	public static Tardis INSTANCE = new Tardis();

	private HashMap<EntityLivingBase, Destination> companions = new HashMap<>();

	private Tardis() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void interpolatePosition(EntityLivingBase entity, Vec3d from, Vec3d to, int maxTicks) {
		companions.put(entity, new Destination(entity, from, to, maxTicks));
	}

	@SubscribeEvent
	public void renderTick(TickEvent.RenderTickEvent event) {

		HashMap<EntityLivingBase, Destination> tmp = new HashMap<>(companions);

		for (EntityLivingBase entity : tmp.keySet()) {
			Destination dest = companions.get(entity);

			Vec3d current = entity.getPositionVector();
			Vec3d to = dest.getTo();

			double dist = current.distanceTo(to);

			if (dist < 0.3) {
				companions.remove(entity);
				entity.setNoGravity(false);
			} else {
				entity.setNoGravity(true);

				double sub = (dest.getLastWorldTime() - entity.world.getTotalWorldTime()) + event.renderTickTime;
				if (sub > dest.getMaxTicks()) {
					companions.remove(entity);
					entity.setNoGravity(false);
				}

				float percent = (float) (sub / dest.getMaxTicks());

				Vec3d newCurrent = dest.getFrom().add(dest.getTo().subtract(dest.getFrom()).scale(percent));

				entity.setPositionAndUpdate(newCurrent.x, newCurrent.y, newCurrent.z);
			}
		}
	}

	public class Destination {

		private final Vec3d from;
		private final Vec3d to;
		private final long lastWorldTime;
		private final int maxTicks;

		Destination(EntityLivingBase entity, Vec3d from, Vec3d to, int maxTicks) {
			this.from = from;
			this.to = to;
			this.lastWorldTime = entity.world.getTotalWorldTime();
			this.maxTicks = maxTicks;
		}

		public int getMaxTicks() {
			return maxTicks;
		}

		public long getLastWorldTime() {
			return lastWorldTime;
		}

		public Vec3d getFrom() {
			return from;
		}

		public Vec3d getTo() {
			return to;
		}
	}
}
