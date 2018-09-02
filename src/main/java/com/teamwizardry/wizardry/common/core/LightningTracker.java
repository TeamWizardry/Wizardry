package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.api.LightningGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.LinkedList;

public class LightningTracker {
	public static LightningTracker INSTANCE = new LightningTracker();

	private HashMap<Entity, TrackingEntry> entityToEntry = new HashMap<>();
	private LinkedList<TrackingEntry> newEntries = new LinkedList<>();

	private LightningTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addEntity(Vec3d origin, Entity target, Entity caster, double potency, double duration) {
		double dist = target.getPositionVector().subtract(origin).length();
		int numPoints = (int) (dist * LightningGenerator.POINTS_PER_DIST);
		newEntries.add(new TrackingEntry(numPoints, potency, duration, caster, target));
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event) {
		newEntries.stream().forEach(e -> entityToEntry.put(e.getTarget(), e));
		newEntries.clear();
		
		entityToEntry.keySet().removeIf(entity -> {
			TrackingEntry entry = entityToEntry.get(entity);
			Entity caster = entry.getCaster();
			int ticks = entry.getTicks();
			double potency = entry.getPotency();
			double duration = entry.getDuration();
			
			if (ticks > 0) {
				entry.setTicks(ticks - 1);
				return false;
			}

			entity.setFire((int) duration);
		
			if (caster instanceof EntityPlayer)
				entity.attackEntityFrom(new EntityDamageSource("lightningbolt", caster), (float) potency);
			else entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, (float) potency);
			return true;
		});
	}
	
	private static class TrackingEntry {
		private int ticks;
		private final double potency;
		private final double duration;
		private final Entity caster;
		private final Entity target;
				
		TrackingEntry(int ticks, double potency, double duration, Entity caster, Entity target) {
			super();
			this.ticks = ticks;
			this.potency = potency;
			this.duration = duration;
			this.caster = caster;
			this.target = target;
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
		
		public final double getDuration() {
			return duration;
		}

		public final Entity getCaster() {
			return caster;
		}
		
		public final Entity getTarget() {
			return target;
		}
	}
}
