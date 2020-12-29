package com.teamwizardry.wizardry.api.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IManaNode
{
    public enum ManaNodeType
    {
        SOURCE,
        SINK,
        ROUTER
    }
    
    public ManaNodeType getManaNodeType();
    
    public default boolean addMana(World world, BlockPos pos, double amount) { return false; }
    
    public default boolean removeMana(World world, BlockPos pos, double amount) { return false; }
    
    public default double getMana(World world, BlockPos pos) { return 0; }
    
    public default double getMaxMana(World world, BlockPos pos) { return 0; }
    
    public default double getMissingMana(World world, BlockPos pos) { return this.getMaxMana(world, pos) - this.getMana(world, pos); }
}
