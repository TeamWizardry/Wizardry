package com.teamwizardry.wizardry.common.nemez;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.common.network.PacketNemezReversal;
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

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Objects;

/**
 * Handles time reversal ticking on both client and server
 */
public final class NemezEventHandler {

	private static HashSet<Reversal> reversals = new HashSet<>();

	public static void register() {
		MinecraftForge.EVENT_BUS.register(NemezEventHandler.class);
	}

	@SideOnly(Side.CLIENT)
	public static NemezTracker getCurrent() {
		if (reversals.isEmpty()) {
			Reversal reversal = new Reversal(Minecraft.getMinecraft().world, new NemezTracker());
			reversals.add(reversal);
		}
		return reversals.iterator().next().nemez;
	}

	public static void reverseTime(World world, NemezTracker tracker, BlockPos locus) {
		Reversal reversal = new Reversal(world, tracker);
		reversal.pos = locus;
		reversals.add(reversal);
	}

	public static void rewind(World world, NemezTracker tracker, BlockPos locus) {
		reverseTime(world, tracker, locus);
		tracker.erase();
	}


	@SubscribeEvent
	public static void worldTick(TickEvent.WorldTickEvent event) {
		if (event.phase == TickEvent.Phase.START)
			reversals.removeIf((reversal) -> {
				if (reversal.world.get() == event.world) {
					if (reversal.nemez.hasNext()) {
						reversal.nemez.applySnapshot(event.world);
						if (reversal.pos != null && reversal.world.get().getTotalWorldTime() % PacketNemezReversal.SYNC_AMOUNT == 0)
							PacketHandler.NETWORK.sendToAllAround(new PacketNemezReversal(reversal.nemez),
									new NetworkRegistry.TargetPoint(reversal.world.get().provider.getDimension(),
											reversal.pos.getX() + 0.5, reversal.pos.getY() + 0.5, reversal.pos.getZ() + 0.5, 96));
					} else {
						for (Entity entity : reversal.nemez.getTrackedEntities(event.world))
							entity.setNoGravity(false);
						return true;
					}
				}
				return reversal.world.get() == null;
			});
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderTick(TickEvent.RenderTickEvent event) {
		World world = Minecraft.getMinecraft().world;
		if (event.phase == TickEvent.Phase.START) {
			for (Reversal reversal : reversals) {
				if (reversal.world.get() != world) continue;

				if (reversal.nemez.hasNext())
					reversal.nemez.applySnapshot(world, event.renderTickTime);
			}
		}
	}

	private static class Reversal {

		private final WeakReference<World> world;
		private final NemezTracker nemez;

		@Nullable
		private BlockPos pos = null;

		public Reversal(World world, NemezTracker tracker) {
			this.world = new WeakReference<>(world);
			this.nemez = tracker.snapshot();
			this.nemez.collapse();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Reversal reversal = (Reversal) o;
			return Objects.equals(world, reversal.world) &&
					Objects.equals(nemez, reversal.nemez);
		}

		@Override
		public int hashCode() {
			return Objects.hash(world, nemez);
		}
	}
}
