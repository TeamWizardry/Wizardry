package com.teamwizardry.wizardry.common.block

import com.teamwizardry.wizardry.api.capability.mana.IManaCapability

interface IManaNode {
    enum class ManaNodeType {
        SOURCE, SINK, ROUTER
    }

    val manaNodeType: ManaNodeType

    /**
     * Add the given amount of mana to the block at the given location.
     * Default functionality assumes storage is via a `BlockEntity`
     * with an attached [IManaCapability]
     * @return The amount of mana unable to be added
     */
    fun addMana(world: World?, pos: BlockPos?, amount: Double): Double {
//        BlockEntity te = world.getBlockEntity(pos);
//        IManaCapability cap = te.getCapability(MANA_CAPABILITY).orElse(null);
//        if (cap == null)
        return amount
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
     * Default functionality assumes storage is via a `BlockEntity`
     * with an attached [IManaCapability]
     * @return The amount of mana unable to be removed
     */
    fun removeMana(world: World?, pos: BlockPos?, amount: Double): Double {
//        BlockEntity te = world.getBlockEntity(pos);
//        IManaCapability cap = te.getCapability(MANA_CAPABILITY).orElse(null);
//        if (cap == null)
        return amount
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
     * Default functionality assumes storage is via a `BlockEntity`
     * with an attached [IManaCapability]
     * @return The amount of mana in the given block, 0 if it has no storage
     */
    fun getMana(world: World?, pos: BlockPos?): Double {
        return 0
        //        BlockEntity te = world.getBlockEntity(pos);
//        IManaCapability cap = te.getCapability(MANA_CAPABILITY).orElse(null);
//        return cap == null ? 0 : cap.getMana();
    }

    /**
     * Tells how much mana the block at the given location may contain.
     * Default functionality assumes storage is via a `BlockEntity`
     * with an attached [IManaCapability]
     * @return The size of the given block's mana pool, 0 if it has no storage
     */
    fun getMaxMana(world: World?, pos: BlockPos?): Double {
        return 0
        //        BlockEntity te = world.getBlockEntity(pos);
//        IManaCapability cap = te.getCapability(MANA_CAPABILITY).orElse(null);
//        return cap == null ? 0 : cap.getMaxMana();
    }

    /**
     * Tells how much mana the block at the given location is missing.
     * Default functionality assumes storage is via a `BlockEntity`
     * with an attached [IManaCapability]
     * @return The amount of mana to add to the given block to fill its pool
     */
    fun getMissingMana(world: World?, pos: BlockPos?): Double {
        return getMaxMana(world, pos) - getMana(world, pos)
    }

    /**
     * Moves all the mana from one block to another, up to a cap
     * @param source The block the mana is transfered from
     * @param sink The block the mana is transfered to
     * @param amount The maximum amount of mana to transfer
     */
    fun transferMana(world: World?, source: BlockPos?, sink: BlockPos?, amount: Double) {
        var amount = amount
        val fromSource = getMana(world, source)
        val toSink = getMissingMana(world, sink)
        if (amount > fromSource) amount = fromSource
        if (amount > toSink) amount = toSink
        removeMana(world, source, amount)
        addMana(world, sink, amount)
    }
}