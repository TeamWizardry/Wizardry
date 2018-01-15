package com.teamwizardry.wizardry.api.arena;

import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.saving.SaveInPlace;
import com.teamwizardry.wizardry.common.entity.angel.EntityAngel;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.EntityZachriel;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.ZachHourGlass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

@SaveInPlace
public class ArenaManager {

	public static ArenaManager INSTANCE = new ArenaManager();

	public HashSet<ZachHourGlass> zachHourGlasses = new HashSet<>();
	@Save
	private HashSet<Arena> arenas = new HashSet<>();

	private ArenaManager() {
		new Timer().scheduleAtFixedRate(new ArenaTicker(), 0, 1);
	}

	public boolean addArena(@NotNull Arena arena) {
		for (Arena arena1 : arenas) {
			if (arena.getCenter().toLong() == arena1.getCenter().toLong()) return false;
		}
		arenas.add(arena);

		if (arena.getBoss() instanceof EntityZachriel) {
			ArenaManager.INSTANCE.zachHourGlasses.add(new ZachHourGlass((EntityZachriel) arena.getBoss()));
		}

		return true;
	}

	@Nullable
	public ZachHourGlass getZachHourGlass(EntityZachriel zach) {
		for (ZachHourGlass glass : zachHourGlasses) {
			if (glass.getEntityZachriel().getEntityId() == zach.getEntityId()) return glass;
		}
		return null;
	}

	@Nullable
	public Arena getArena(@NotNull EntityAngel boss) {
		for (Arena arena : arenas) {
			if (arena.getBossID() == boss.getEntityId()) return arena;
		}
		return null;
	}

	@SubscribeEvent
	public void tickBoss(LivingEvent.LivingUpdateEvent event) {
		for (Arena arena : arenas) {
			if (event.getEntityLiving().getEntityId() != arena.getBossID()) continue;

			if (event.getEntityLiving().getDistance(arena.getCenter().getX() + 0.5, arena.getCenter().getY(), arena.getCenter().getZ() + 0.5) > arena.getRadius()) {
				event.getEntityLiving().move(MoverType.SELF, arena.getCenter().getX() + 0.5, arena.getCenter().getY() + 0.5, arena.getCenter().getZ() + 0.5);
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

			if (!event.getWorld().isRemote)
				for (ZachHourGlass glass : zachHourGlasses) {
					if (glass.getEntityZachriel().getEntityId() == arena.getBossID()) {
						glass.trackBlockTick(event.getPos(), event.getState());
					}
				}

//			if (!event.getWorld().isRemote)
//				for (ZachTimeManager timeManager : zachHourGlasses) {
//					if (timeManager.getEntityZachriel().getEntityId() == arena.getBossID()) {
//						timeManager.trackBlock(event.getState(), event.getPos());
//					}
//				}
		}
	}

	@SubscribeEvent
	public void placeBlock(BlockEvent.PlaceEvent event) {
		for (Arena arena : arenas) {
			if (!arena.getPlayers().contains(event.getPlayer().getUniqueID())) {
				continue;
			}

			if (!event.getWorld().isRemote)
				for (ZachHourGlass glass : zachHourGlasses) {
					if (glass.getEntityZachriel().getEntityId() == arena.getBossID()) {
						glass.trackBlockTick(event.getPos(), Blocks.AIR.getDefaultState());
					}
				}

			//if (!event.getWorld().isRemote)
			//	for (ZachTimeManager timeManager : zachHourGlasses) {
			//		if (timeManager.getEntityZachriel().getEntityId() == arena.getBossID()) {
			//			timeManager.trackBlock(Blocks.AIR.getDefaultState(), event.getPos());
			//		}
			//	}
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
