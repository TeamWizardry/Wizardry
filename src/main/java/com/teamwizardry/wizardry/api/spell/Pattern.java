package com.teamwizardry.wizardry.api.spell;

import java.util.Map;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Stores the methods that run the actual spell effects.
 * Attached to Modules created using the {@code modid:name} pair the pattern was
 * registered under
 */
public abstract class Pattern extends ForgeRegistryEntry<Pattern>
{
    // NBT Tag Keys
    protected static final String CASTER = "caster";
    protected static final String SOURCE = "source";
    // If caster or source is an entity, data will contain a UUID
    // If caster or source is a block, data will be a compound tag with POS and DIR tags
    protected static final String POS = "pos";
    protected static final String DIR = "dir";
    
    public abstract void run(World world, Interactor caster, Interactor source, Interactor target, Map<String, Double> attributeValues, double manaCost, double burnoutCost);
    public abstract void affectEntity(World world, LivingEntity entity);

    public abstract void affectBlock(World world, BlockPos pos);
}
