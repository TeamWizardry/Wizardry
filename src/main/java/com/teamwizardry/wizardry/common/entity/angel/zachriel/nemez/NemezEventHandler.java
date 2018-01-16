package com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.EntityZachriel;
import com.teamwizardry.wizardry.common.network.PacketZachrielTimeReversal;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;

/**
 * Handles time reversal ticking on both client and server
 */
public final class NemezEventHandler {

	public static NemezEventHandler INSTANCE = new NemezEventHandler();
	private static HashSet<Reversal> reversals = new HashSet<>();

	public static void register() {
		MinecraftForge.EVENT_BUS.register(NemezEventHandler.class);
	}

	public static void reverseTime(World world, NemezArenaTracker tracker) {
		Reversal reversal = new Reversal(world, tracker);
		reversals.add(reversal);
	}

	public static void reverseTime(World world, NemezArenaTracker tracker, BlockPos position) {
		reverseTime(world, tracker);
		if (!world.isRemote)
			PacketHandler.NETWORK.sendToAllAround(new PacketZachrielTimeReversal(tracker),
					new NetworkRegistry.TargetPoint(world.provider.getDimension(),
							position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, 96));
	}

	public static void reverseTime(EntityZachriel zachriel) {
		reverseTime(zachriel.world, zachriel.nemezDrive, zachriel.arena.getCenter());
		zachriel.nemezDrive.erase();
	}



	@SubscribeEvent
	public static void worldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {

			HashSet<Reversal> tmp = new HashSet<>(reversals);
			for (Reversal watch : tmp) {
				if (watch.world != event.world) continue;

				if (watch.nemez.hasNext()) {
					watch.nemez.applySnapshot(event.world);
				} else {
					for (Entity entity : watch.nemez.getTrackedEntities(event.world))
						entity.setNoGravity(false);

					reversals.remove(watch);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderTick(TickEvent.RenderTickEvent event) {
		World world = Minecraft.getMinecraft().world;
		if (event.phase == TickEvent.Phase.START) {

			HashSet<Reversal> tmp = new HashSet<>(reversals);
			for (Reversal watch : tmp) {
				if (watch.world != world) continue;

				if (watch.nemez.hasNext())
					watch.nemez.applySnapshot(world, event.renderTickTime);
			}
		}
	}

	private static class Reversal {

		private final World world;
		private NemezArenaTracker nemez;

		public Reversal(World world, NemezArenaTracker tracker) {
			this.world = world;
			this.nemez = tracker.snapshot();
			this.nemez.collapse();
		}

		public World getWorld() {
			return world;
		}
	}
}
