package com.teamwizardry.wizardry.common.core;

import java.util.HashMap;

import com.teamwizardry.wizardry.api.LightningGenerator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LightningTracker
{
	public static LightningTracker INSTANCE = new LightningTracker();
	
	private HashMap<Entity, Integer> entityToTicks = new HashMap<>();
	private HashMap<Entity, Entity> entityToCaster = new HashMap<>();
	private HashMap<Entity, Integer> entityToStrength = new HashMap<>();
	
	private LightningTracker()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void addEntity(Vec3d origin, Entity target, Entity caster, int strength)
	{
		double dist = target.getPositionVector().subtract(origin).lengthVector();
		int numPoints = (int) (dist * LightningGenerator.POINTS_PER_DIST);
		entityToTicks.put(target, numPoints);
		entityToCaster.put(target, caster);
		entityToStrength.put(target, strength);
	}
	
	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event)
	{
		entityToTicks.keySet().removeIf(entity -> {
			Entity caster = entityToCaster.get(entity);
			int ticks = entityToTicks.get(entity);
			int strength = entityToStrength.get(entity);
			
			if (ticks > 0)
			{
				entityToTicks.put(entity, --ticks);
				return false;
			}
			
			entityToCaster.remove(entity);
			entityToStrength.remove(entity);
			
			entity.setFire((int) (strength));
			if (caster instanceof EntityPlayer)
				entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) caster), (float) (strength));
			else entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, (float) (strength));
			return true;
		});
	}
}
