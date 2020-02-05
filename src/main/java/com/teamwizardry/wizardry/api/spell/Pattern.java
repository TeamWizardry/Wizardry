package com.teamwizardry.wizardry.api.spell;

import java.util.function.Function;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

/**
 * Stores the methods that run the actual spell effects.
 * Attached to Modules created using the {@code modid:name} pair registered in
 * {@link PatternRegistry}
 * 
 * @see PatternRegistry
 * @see Module
 */
public abstract class Pattern
{
    protected Function<Entity, Boolean> shouldAffectEntity = EntityTarget.ALWAYS;
    protected Function<BlockPos, Boolean> shouldAffectBlock = BlockTarget.ALWAYS;
    
    public abstract void run();
    
    public abstract void affectEntity(Entity entity);
    
    public abstract void affectBlock(BlockPos pos);
}
