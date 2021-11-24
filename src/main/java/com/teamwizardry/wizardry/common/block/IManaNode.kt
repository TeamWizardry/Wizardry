package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.wizardry.api.capability.mana.IManaCapability;

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
    
    /**
     * Add the given amount of mana to the block at the given location.
     * Default functionality assumes storage is via a {@code BlockEntity}
     * with an attached {@link IManaCapability}
     * @return The amount of mana unable to be added
     */
    public default double addMana(World world, BlockPos pos, double amount)
    {
//        BlockEntity te = world.getBlockEntity(pos);
//        IManaCapability cap = te.getCapability(MANA_CAPABILITY).orElse(null);
//        if (cap == null)
            return amount;
//        double current = cap.getMana();
//        double available = cap.getMaxMana() - current;
//        if (available >= amount)
//        {
//            cap.setMana(current + amount);
//            return 0;
//        }
//        cap.setMana(cap.getMaxMana());
//        return amount - available;
    }
    
    /**
     * Removes the given amount of mana from the block at the given location.
     * Default functionality assumes storage is via a {@code BlockEntity}
     * with an attached {@link IManaCapability}
     * @return The amount of mana unable to be removed
     */
    public default double removeMana(World world, BlockPos pos, double amount)
    {
//        BlockEntity te = world.getBlockEntity(pos);
//        IManaCapability cap = te.getCapability(MANA_CAPABILITY).orElse(null);
//        if (cap == null)
            return amount;
//        double current = cap.getMana();
//        if (current >= amount)
//        {
//            cap.setMana(current - amount);
//            return 0;
//        }
//        cap.setMana(0);
//        return amount - current;
    }
    
    /**
     * Tells how much mana the block at the given location contains.
     * Default functionality assumes storage is via a {@code BlockEntity}
     * with an attached {@link IManaCapability}
     * @return The amount of mana in the given block, 0 if it has no storage
     */
    public default double getMana(World world, BlockPos pos)
    {
        return 0;
//        BlockEntity te = world.getBlockEntity(pos);
//        IManaCapability cap = te.getCapability(MANA_CAPABILITY).orElse(null);
//        return cap == null ? 0 : cap.getMana();
    }
    
    /**
     * Tells how much mana the block at the given location may contain.
     * Default functionality assumes storage is via a {@code BlockEntity}
     * with an attached {@link IManaCapability}
     * @return The size of the given block's mana pool, 0 if it has no storage
     */
    public default double getMaxMana(World world, BlockPos pos)
    {
        return 0;
//        BlockEntity te = world.getBlockEntity(pos);
//        IManaCapability cap = te.getCapability(MANA_CAPABILITY).orElse(null);
//        return cap == null ? 0 : cap.getMaxMana();
    }
    
    /**
     * Tells how much mana the block at the given location is missing.
     * Default functionality assumes storage is via a {@code BlockEntity}
     * with an attached {@link IManaCapability}
     * @return The amount of mana to add to the given block to fill its pool
     */
    public default double getMissingMana(World world, BlockPos pos)
    {
        return this.getMaxMana(world, pos) - this.getMana(world, pos);
    }
    
    /**
     * Moves all the mana from one block to another, up to a cap
     * @param source The block the mana is transfered from
     * @param sink The block the mana is transfered to
     * @param amount The maximum amount of mana to transfer
     */
    public default void transferMana(World world, BlockPos source, BlockPos sink, double amount)
    {
        double fromSource = this.getMana(world, source);
        double toSink = this.getMissingMana(world, sink);
        
        if (amount > fromSource)
            amount = fromSource;
        if (amount > toSink)
            amount = toSink;
        this.removeMana(world, source, amount);
        this.addMana(world, sink, amount);
    }
}
