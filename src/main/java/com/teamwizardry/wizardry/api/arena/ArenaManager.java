package com.teamwizardry.wizardry.api.arena;

import com.teamwizardry.librarianlib.features.saving.Savable;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

@Savable
public class ArenaManager {

	public static ArenaManager INSTANCE = new ArenaManager();

	private HashSet<Arena> arenas = new HashSet<>();

	private ArenaManager() {
		new Timer().scheduleAtFixedRate(new ArenaTicker(), 0, 1);
	}

	public Arena addArena(@NotNull Arena arena) {
		arenas.add(arena);
		return arena;
	}

	@SubscribeEvent
	public void tickBoss(LivingEvent.LivingUpdateEvent event) {
		for (Arena arena : arenas) {
			if (event.getEntityLiving().getEntityId() != arena.getBossID()) continue;

			if (event.getEntityLiving().getDistance(arena.getCenter().getX() + 0.5, arena.getCenter().getY(), arena.getCenter().getZ() + 0.5) > arena.getRadius()) {
				arena.end();
			}
		}
	}

	@SubscribeEvent
	public void tickPlayer(TickEvent.PlayerTickEvent event) {
		for (Arena arena : arenas) {
			if (!arena.getPlayers().contains(event.player.getUniqueID())) {
				continue;
			}
			if (event.player.getDistance(arena.getCenter().getX() + 0.5, arena.getCenter().getY(), arena.getCenter().getZ() + 0.5) > arena.getRadius()) {
				arena.end();
				continue;
			}
			if (event.player.capabilities.isFlying) {
				event.player.capabilities.isFlying = false;
			}
		}
	}

	@SubscribeEvent
	public void blockBreak(BlockEvent.BreakEvent event) {
		for (Arena arena : arenas) {
			if (!arena.getPlayers().contains(event.getPlayer().getUniqueID())) {
				continue;
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void placeBlock(BlockEvent.PlaceEvent event) {
		for (Arena arena : arenas) {
			if (!arena.getPlayers().contains(event.getPlayer().getUniqueID())) {
				continue;
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderWorld(RenderWorldLastEvent event) {
		for (Arena arena : arenas) {

		}
	}

	private class ArenaTicker extends TimerTask {

		ArenaTicker() {
		}

		@Override
		public void run() {
			if (arenas.isEmpty()) return;

			HashSet<Arena> trash = new HashSet<>();
			for (Arena arena : arenas) {
				if (arena == null) continue;
				if (arena.hasEnded()) {
					trash.add(arena);
				}
				if (!arena.isActive()) continue;
				Entity entity = arena.getWorld().getEntityByID(arena.getBossID());
				if (entity == null || entity.isDead) {
					arena.end();
					continue;
				}
				//if (!arena.sanityCheck()) {
				//	arena.dealWithStructureConflict();
				//	continue;
				//}

				arena.tick(System.currentTimeMillis() - arena.getStartTick());
			}

			arenas.removeAll(trash);
		}
	}
}
