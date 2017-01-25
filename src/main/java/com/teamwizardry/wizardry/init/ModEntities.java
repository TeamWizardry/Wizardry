package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.render.entity.ModelFairy;
import com.teamwizardry.wizardry.client.render.entity.ModelHallowedSpirit;
import com.teamwizardry.wizardry.client.render.entity.RenderFairy;
import com.teamwizardry.wizardry.client.render.entity.RenderHallowedSpirit;
import com.teamwizardry.wizardry.common.entity.EntityDevilDust;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.entity.EntityHallowedSpirit;
import com.teamwizardry.wizardry.common.entity.EntitySpellCodex;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by Saad on 8/17/2016.
 */
public class ModEntities {

	private static int i = 0;

	public static void init() {
		registerEntity(EntityHallowedSpirit.class, "hallowed_spirit", 64, 3, true);
		registerEntity(EntityFairy.class, "fairy", 64, 3, true);
		registerEntity(EntityDevilDust.class, "dust_tracker", 64, 1, false);
		registerEntity(EntitySpellCodex.class, "book_tracker", 64, 1, false);
	}
	
	public static void registerEntity(Class<? extends Entity> entityClass, String entityName) {
		registerEntity(entityClass, entityName, 80, 3, true);
	}
	
	//Use when default parameters are not sufficient, e.g fast-moving projectiles
	public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(entityClass, entityName, i, Wizardry.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
		System.out.println(i);
		i++;
	}

	public static void initModels() {
		RenderingRegistry.registerEntityRenderingHandler(EntityHallowedSpirit.class, manager -> new RenderHallowedSpirit(manager, new ModelHallowedSpirit()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFairy.class, manager -> new RenderFairy(manager, new ModelFairy()));
	}
}
