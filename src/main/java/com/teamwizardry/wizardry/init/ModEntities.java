package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.entity.EntityHallowedSprit;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by Saad on 8/17/2016.
 */
public class ModEntities {

	public static void init() {
		EntityRegistry.registerModEntity(EntityHallowedSprit.class, "hallowed_spirit", 0, Wizardry.instance, 64, 3, true);
	}
}
