package com.teamwizardry.wizardry.common.entity.angel.zachriel;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.arena.ArenaManager;
import com.teamwizardry.wizardry.common.network.PacketZachClearCompanions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Handles time reversal ticking on both client and server
 */
public class Tardis {

	public static Tardis INSTANCE = new Tardis();

	private HashMap<EntityLivingBase, Destination> companions = new HashMap<>();
	private HashSet<PocketWatch> pocketWatches = new HashSet<>();

	private Tardis() {
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
		//Minecraft.getMinecraft().player.sendChatMessage("Pocket watch made.");
	}

	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {

			if (event.world.getTotalWorldTime() % 5 != 0) return;

			HashSet<PocketWatch> tmp = new HashSet<>(pocketWatches);
			for (PocketWatch watch : tmp) {
				//Minecraft.getMinecraft().player.sendChatMessage("Loop pocket watches - " + pocketWatches.size());

				// Delete object if glass is null //
				ZachHourGlass glass = watch.getGlass();
				if (glass == null) {
					Minecraft.getMinecraft().player.sendChatMessage("Hour glass is null");
					pocketWatches.remove(watch);
					continue;
				}
				// Delete object if glass is null //

				//	if (glass.getEntityZachriel().world.getTotalWorldTime() % 50 != 0) continue;

				if (watch.timeReverseTicks > 0) {

					//// --- REVERSE TIME --- ////

					Minecraft.getMinecraft().player.sendChatMessage("maxTick: " + watch.timeReverseTicks);

					watch.timeReverseTicks--;

					// Reverse Entities //
					for (UUID uuid : glass.getAllTrackedEntities()) {
						ZachHourGlass.EntityState state = glass.getEntityStateAtTime(uuid, watch.timeReverseTicks);
						if (state == null) continue;

						for (Entity entity : watch.getWorld().getEntities(Entity.class, input -> input instanceof EntityLivingBase)) {
							if (entity.getUniqueID().equals(uuid)) {
								entity.setNoGravity(true);
								state.setToEntity((EntityLivingBase) entity);
							}
						}
					}
					// Reverse Entities //

					// Reverse Blocks //
					for (BlockPos pos : glass.getAllTrackedBlocks()) {
						ZachHourGlass.BlockStateState state = glass.getBlockStateAtTime(pos, watch.timeReverseTicks);
						if (state == null) continue;

						watch.getWorld().setBlockState(pos, state.getState());
					}
					// Reverse Blocks //

					//// --- REVERSE TIME --- ////

				} else {
					//// --- FINISHED REVERSING - RESET --- ////

					// Reset gravity for all entities //
					for (UUID uuid : glass.getAllTrackedEntities()) {
						ZachHourGlass.EntityState state = glass.getEntityStateAtTime(uuid, watch.timeReverseTicks);
						if (state == null) continue;

						for (Entity entity : watch.getWorld().getEntities(Entity.class, input -> input instanceof EntityLivingBase)) {
							if (entity.getUniqueID().equals(uuid)) {
								entity.setNoGravity(false);
							}
						}
					}

					watch.timeReverseTicks = 200;

					// Delete all info saved //
					glass.setTracking(false, true);

					// Let the object die in a fire //
					pocketWatches.remove(watch);

					PacketHandler.NETWORK.sendToAll(new PacketZachClearCompanions(watch.getZach().getEntityId()));
					//// --- FINISHED REVERSING - RESET --- ////
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

		private final EntityZachriel zach;
		private final World world;
		public int timeReverseTicks = 200;
		@Nullable
		private ZachHourGlass glass;

		public PocketWatch(@NotNull EntityZachriel zach) {
			this.zach = zach;
			this.world = zach.world;
			glass = ArenaManager.INSTANCE.getZachHourGlass(zach);

			if (glass != null)
				glass.setTracking(false, false);
		}

		@Nullable
		public ZachHourGlass getGlass() {
			return glass;
		}

		public EntityZachriel getZach() {
			return zach;
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
