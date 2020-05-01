package com.teamwizardry.wizardry.api.spell;

import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Stores the methods that run the actual spell effects.
 * Attached to Modules created using the {@code modid:name} pair the pattern was
 * registered under
 */
public abstract class Pattern extends ForgeRegistryEntry<Pattern>
{
    public abstract void run(Function<BlockPos, Boolean> shouldAffectBlock, Function<Entity, Boolean> shouldAffectEntity);

    public abstract void affectEntity(Entity entity);

    public abstract void affectBlock(BlockPos pos);
}
