package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.api.LightningGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;

public class LightningTracker {
	public static LightningTracker INSTANCE = new LightningTracker();

	private HashMap<Entity, TrackingEntry> entityToEntry = new HashMap<>();

	private LightningTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addEntity(Vec3d origin, Entity target, Entity caster, double potency) {
		double dist = target.getPositionVector().subtract(origin).lengthVector();
		int numPoints = (int) (dist * LightningGenerator.POINTS_PER_DIST);
		entityToEntry.put(target, new TrackingEntry(numPoints, dist, caster));
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event) {
		entityToEntry.keySet().removeIf(entity -> {
			TrackingEntry entry = entityToEntry.get(entity);
			Entity caster = entry.getCaster();
			int ticks = entry.getTicks();
			double potency = entry.getPotency();
			
			if (ticks > 0) {
				entry.setTicks(ticks - 1);
				return false;
			}

 			entity.setFire((int) potency);
			if (caster instanceof EntityPlayer)
				entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) caster), (float) potency);
			else entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, (float) potency);
			return true;
		});
	}
	
	private static class TrackingEntry {
		private int ticks;
		private final double potency;
		private final Entity caster;
		
		TrackingEntry(int ticks, double potency, Entity caster) {
			super();
			this.ticks = ticks;
			this.potency = potency;
			this.caster = caster;
		}
		
		public final void setTicks( int ticks ) {
			this.ticks = ticks;
		}
		
		public final int getTicks() {
			return ticks;
		}

		public final double getPotency() {
			return potency;
		}

		public final Entity getCaster() {
			return caster;
		}
	}
}
