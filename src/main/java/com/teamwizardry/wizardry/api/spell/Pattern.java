package com.teamwizardry.wizardry.api.spell;

import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;


/**
 * Stores the methods that run the actual spell effects.
 * Attached to Modules created using the {@code modid:name} pair the pattern was
 * registered under
 */
public abstract class Pattern extends ForgeRegistryEntry<Pattern> {
    public void run(World world, Instance instance, Interactor target) {
        if (instance == null) return;
        if (instance.getCaster() == null || target == null)
            return;

        instance.getCaster().consumeCost(world, instance.getManaCost(), instance.getBurnoutCost());
        switch (target.getType()) {
            case BLOCK: {
                affectBlock(world, target, instance);
                return;
            }
            case ENTITY: {
                affectEntity(world, target, instance);
                return;
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void runClient(World world, Instance instance, Interactor target) {
    }

    public abstract void affectEntity(World world, Interactor entity, Instance instance);

    public abstract void affectBlock(World world, Interactor block, Instance instance);
}
