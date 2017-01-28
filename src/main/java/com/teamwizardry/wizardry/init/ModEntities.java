package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.render.entity.ModelFairy;
import com.teamwizardry.wizardry.client.render.entity.ModelHallowedSpirit;
import com.teamwizardry.wizardry.client.render.entity.RenderFairy;
import com.teamwizardry.wizardry.client.render.entity.RenderHallowedSpirit;
import com.teamwizardry.wizardry.common.entity.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by Saad on 8/17/2016.
 */
public class ModEntities {

	private static int i = 0;

	public static void init() {
		EntityRegistry.registerModEntity(EntityHallowedSpirit.class, "hallowed_spirit", i++, Wizardry.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityFairy.class, "fairy", i++, Wizardry.instance, 64, 3, true);
		EntityRegistry.registerModEntity(EntityDevilDust.class, "dust_tracker", i++, Wizardry.instance, 64, 1, false);
		EntityRegistry.registerModEntity(EntitySpellCodex.class, "book_tracker", i++, Wizardry.instance, 64, 1, false);
		EntityRegistry.registerModEntity(EntitySpellProjectile.class, "spell_projectile", i++, Wizardry.instance, 64, 1, false);
	}

	public static void initModels() {
		RenderingRegistry.registerEntityRenderingHandler(EntityHallowedSpirit.class, manager -> new RenderHallowedSpirit(manager, new ModelHallowedSpirit()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFairy.class, manager -> new RenderFairy(manager, new ModelFairy()));
	}
}
