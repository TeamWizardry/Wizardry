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

	private HashMap<Entity, Integer> entityToTicks = new HashMap<>();
	private HashMap<Entity, Entity> entityToCaster = new HashMap<>();
	private HashMap<Entity, Double> entityToPotency = new HashMap<>();

	private LightningTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addEntity(Vec3d origin, Entity target, Entity caster, double potency) {
		double dist = target.getPositionVector().subtract(origin).lengthVector();
		int numPoints = (int) (dist * LightningGenerator.POINTS_PER_DIST);
		entityToTicks.put(target, numPoints);
		entityToCaster.put(target, caster);
		entityToPotency.put(target, potency);
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event) {
		entityToTicks.keySet().removeIf(entity -> {
			Entity caster = entityToCaster.get(entity);
			int ticks = entityToTicks.get(entity);
			double potency = entityToPotency.get(entity);

			if (ticks > 0) {
				entityToTicks.put(entity, --ticks);
				return false;
			}

			entityToCaster.remove(entity);
			entityToPotency.remove(entity);

			entity.setFire((int) potency);
			if (caster instanceof EntityPlayer)
				entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) caster), (float) potency);
			else entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, (float) potency);
			return true;
		});
	}
}
