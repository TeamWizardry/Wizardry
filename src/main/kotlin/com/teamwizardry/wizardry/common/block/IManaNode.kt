package com.teamwizardry.wizardry.common.block

import com.teamwizardry.wizardry.capability.mana.IManaCapability
import com.teamwizardry.wizardry.capability.network.ManaNetwork
import com.teamwizardry.wizardry.common.init.ModCapabilities
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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
    fun addMana(world: World, pos: BlockPos, amount: Double): Double {
        val blockEntity = world.getBlockEntity(pos)
        val cap = ModCapabilities.MANA.maybeGet(blockEntity).orElse(null) ?: return amount
        val availableSpace = cap.maxMana - cap.mana
        return if (amount <= availableSpace) {
            cap.mana += amount
            0.0
        }
        else {
            cap.mana = cap.maxMana
            amount - availableSpace
        }
    }

    /**
     * Removes the given amount of mana from the block at the given location.
     * Default functionality assumes storage is via a `BlockEntity`
     * with an attached [IManaCapability]
     * @return The amount of mana unable to be removed
     */
    fun removeMana(world: World, pos: BlockPos, amount: Double): Double {
        val blockEntity = world.getBlockEntity(pos)
        val cap = ModCapabilities.MANA.maybeGet(blockEntity).orElse(null) ?: return amount
        val current = cap.mana
        return if (current >= amount) {
            cap.mana -= amount
            0.0
        }
        else {
            cap.mana = 0.0
            amount - current
        }
    }

    /**
     * Tells how much mana the block at the given location contains.
     * Default functionality assumes storage is via a `BlockEntity`
     * with an attached [IManaCapability]
     * @return The amount of mana in the given block, 0 if it has no storage
     */
    fun getMana(world: World, pos: BlockPos): Double {
        val blockEntity = world.getBlockEntity(pos)
        val cap = ModCapabilities.MANA.maybeGet(blockEntity).orElse(null) ?: return 0.0
        return cap.mana
    }

    /**
     * Tells how much mana the block at the given location may contain.
     * Default functionality assumes storage is via a `BlockEntity`
     * with an attached [IManaCapability]
     * @return The size of the given block's mana pool, 0 if it has no storage
     */
    fun getMaxMana(world: World, pos: BlockPos): Double {
        val blockEntity = world.getBlockEntity(pos)
        val cap = ModCapabilities.MANA.maybeGet(blockEntity).orElse(null) ?: return 0.0
        return cap.maxMana
    }

    /**
     * Tells how much mana the block at the given location is missing.
     * Default functionality assumes storage is via a `BlockEntity`
     * with an attached [IManaCapability]
     * @return The amount of mana to add to the given block to fill its pool
     */
    fun getMissingMana(world: World, pos: BlockPos): Double {
        return getMaxMana(world, pos) - getMana(world, pos)
    }

    /**
     * Moves all the mana from one block to another, up to a cap
     * @param source The block the mana is transfered from
     * @param sink The block the mana is transfered to
     * @param amount The maximum amount of mana to transfer
     */
    fun transferMana(world: World, source: BlockPos, sink: BlockPos, amount: Double) {
        var amount = amount
        val fromSource = getMana(world, source)
        val toSink = getMissingMana(world, sink)
        if (amount > fromSource) amount = fromSource
        if (amount > toSink) amount = toSink
        removeMana(world, source, amount)
        addMana(world, sink, amount)
    }

    fun placeManaNode(context: ItemPlacementContext) {
        if (!context.world.isClient)
            ManaNetwork[context.world as ServerWorld].addBlock(context.blockPos)
    }
}