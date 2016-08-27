package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.render.ModelHolder;
import com.teamwizardry.wizardry.client.render.entity.RenderFairy;
import com.teamwizardry.wizardry.client.render.entity.RenderHallowedSpirit;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.entity.EntityHallowedSpirit;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by Saad on 8/17/2016.
 */
public class ModEntities {

    public static void init() {
        EntityRegistry.registerModEntity(EntityHallowedSpirit.class, "hallowed_spirit", 0, Wizardry.instance, 64, 3, true);
        EntityRegistry.registerModEntity(EntityFairy.class, "fairy", 1, Wizardry.instance, 64, 3, true);
    }

    public static void initModels() {
        RenderingRegistry.registerEntityRenderingHandler(EntityHallowedSpirit.class, new RenderHallowedSpirit(Minecraft.getMinecraft().getRenderManager(), ModelHolder.entityModels.get("hallowed_spirit"), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(EntityFairy.class, new RenderFairy(Minecraft.getMinecraft().getRenderManager(), ModelHolder.entityModels.get("fairy"), 0.5f));
    }
}
