package com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez;

import com.teamwizardry.wizardry.common.entity.angel.zachriel.EntityZachriel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Handles time reversal ticking on both client and server
 */
public class NemezTicker {

	public static NemezTicker INSTANCE = new NemezTicker();

	private HashMap<EntityLivingBase, Destination> companions = new HashMap<>();
	private HashSet<PocketWatch> pocketWatches = new HashSet<>();

	private NemezTicker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Called when zach's hour glass sets an entity to a snapshot to interpolate entity position
	 * between 2 time reversal ticks.
	 */
	public void interpolatePosition(EntityLivingBase entity, Vec3d from, Vec3d to, int maxTicks) {
		companions.put(entity, new Destination(entity, from, to, maxTicks));
	}

	public void clearCompanions(EntityLivingBase entity) {
		companions.remove(entity);
	}

	/**
	 * The big daddy method to reverse time. This controls/calls literally everything in this class.
	 */
	public void reverseTime(EntityZachriel zachriel) {
		pocketWatches.add(new PocketWatch(zachriel));
	}

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {

			if (event.world.getTotalWorldTime() % 5 != 0) return;

			HashSet<PocketWatch> tmp = new HashSet<>(pocketWatches);
			for (PocketWatch watch : tmp) {
				if (watch.world != event.world) continue;

				if (watch.nemez.hasNext()) {
					watch.nemez.applySnapshot(event.world);
				} else {
					for (Entity entity : watch.nemez.getTrackedEntities(event.world))
						entity.setNoGravity(false);

					pocketWatches.remove(watch);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
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


	/**
	 * Holds Zach's entity and hour glass holds all arena time data maps
	 */
	public class PocketWatch {

		private final World world;
		private NemezTracker nemez;

		public PocketWatch(@NotNull EntityZachriel zach) {
			this.world = zach.world;
			this.nemez = zach.nemezDrive.snapshot();
			this.nemez.collapse();
			zach.nemezDrive.erase();
		}

		public World getWorld() {
			return world;
		}
	}

	/**
	 * Ticks entity position interpolation between each time reversal tick
	 */
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
