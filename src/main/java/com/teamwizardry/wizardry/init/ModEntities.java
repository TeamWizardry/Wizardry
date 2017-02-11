package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.render.entity.*;
import com.teamwizardry.wizardry.common.entity.*;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

//github.com/TeamWizardry/Wizardry

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
		registerEntity(EntitySpellProjectile.class, "spell_projectile", 64, 1, false);
		registerEntity(EntitySpellGravityWell.class, "gravity_well", 64, 1, false);
		registerEntity(EntityJumpPad.class, "jump_pad", 64, 1, false);
		registerEntity(EntityUnicorn.class, "unicorn");
	}
	
	public static void registerEntity(Class<? extends Entity> entityClass, String entityName) {
		registerEntity(entityClass, entityName, 64, 1, true);
	}
	
	//Use when default parameters are not sufficient, e.g fast-moving projectiles
	public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(entityClass, entityName, i, Wizardry.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
		i++;
	}

	public static void initModels() {
		RenderingRegistry.registerEntityRenderingHandler(EntityHallowedSpirit.class, manager -> new RenderHallowedSpirit(manager, new ModelHallowedSpirit()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFairy.class, manager -> new RenderFairy(manager, new ModelNull()));
		RenderingRegistry.registerEntityRenderingHandler(EntityUnicorn.class, manager -> new RenderUnicorn(manager, new ModelUnicorn()));
		RenderingRegistry.registerEntityRenderingHandler(EntityJumpPad.class, manager -> new RenderJumpPad(manager, new ModelNull()));
	}
}
