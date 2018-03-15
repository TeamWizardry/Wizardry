package com.teamwizardry.wizardry.api.arena;

import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.saving.SaveInPlace;
import com.teamwizardry.wizardry.common.entity.angel.EntityAngel;
import com.teamwizardry.wizardry.common.entity.angel.zachriel.EntityZachriel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

@SaveInPlace
public class ArenaManager {

	public static ArenaManager INSTANCE = new ArenaManager();

	@Save
	private HashSet<Arena> arenas = new HashSet<>();

	private ArenaManager() {
		new Timer().scheduleAtFixedRate(new ArenaTicker(), 0, 1);
	}

	public boolean addArena(@Nonnull Arena arena) {
		for (Arena arena1 : arenas) {
			if (arena.getCenter().toLong() == arena1.getCenter().toLong()) return false;
		}
		arenas.add(arena);

		return true;
	}

	@Nullable
	public Arena getArena(@Nonnull EntityAngel boss) {
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
			if (!arena.getPlayers().contains(event.getPlayer().getUniqueID()) || arena.getWorld() != event.getWorld())
				continue;

			if (!event.getWorld().isRemote && arena.getBoss() instanceof EntityZachriel)
				((EntityZachriel) arena.getBoss()).nemezDrive.trackBlock(event.getPos(), event.getState());


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
			if (!arena.getPlayers().contains(event.getPlayer().getUniqueID()) || arena.getWorld() != event.getWorld())
				continue;

			if (!event.getWorld().isRemote && arena.getBoss() instanceof EntityZachriel)
				((EntityZachriel) arena.getBoss()).nemezDrive.trackBlock(event.getPos(), event.getWorld().getBlockState(event.getPos()));

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
