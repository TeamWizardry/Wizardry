package com.teamwizardry.wizardry.common.block.fluid.mana

import net.minecraft.block.BlockState
import net.minecraft.block.FluidBlock
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import java.util.*

class BlockMana(
    fluid: FlowableFluid?,
    settings: Settings?
) : FluidBlock(fluid, settings) {
    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        // TODO float
        super.onEntityCollision(state, world, pos, entity)
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, rand: Random) {
        // TODO: particles
        super.randomDisplayTick(state, world, pos, rand)
    }
}