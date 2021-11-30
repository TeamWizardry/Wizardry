package com.teamwizardry.wizardry.common.block.fluid.mana

import com.teamwizardry.wizardry.common.init.ModBlocks
import com.teamwizardry.wizardry.common.init.ModFluids
import com.teamwizardry.wizardry.common.init.ModItems
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.fluid.FlowableFluid
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.item.Item
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView

abstract class ManaFluid : FlowableFluid() {

    override fun getFlowing(): Fluid { return ModFluids.FLOWING_MANA }
    override fun getStill(): Fluid { return ModFluids.STILL_MANA }
    override fun getBucketItem(): Item { return ModItems.manaBucket }

    override fun matchesType(fluid: Fluid): Boolean { return fluid === still || fluid === flowing }

    override fun isInfinite(): Boolean { return false }

    override fun beforeBreakingBlock(world: WorldAccess, pos: BlockPos, state: BlockState) {
        val entity: BlockEntity? = if (state.hasBlockEntity()) world.getBlockEntity(pos) else null
        Block.dropStacks(state, world, pos, entity)
    }

    override fun getFlowSpeed(world: WorldView): Int { return 4 }

    override fun getLevelDecreasePerBlock(world: WorldView): Int { return 1 }

    override fun canBeReplacedWith(state: FluidState, world: BlockView, pos: BlockPos, fluid: Fluid, direction: Direction): Boolean { return false }

    override fun getTickRate(world: WorldView): Int { return 2 }

    override fun getBlastResistance(): Float { return 100.0f }

    override fun toBlockState(state: FluidState): BlockState {
        return ModBlocks.liquidMana.defaultState.with(Properties.LEVEL_15, getBlockStateLevel(state))
    }

    open class Flowing : ManaFluid() {
        override fun appendProperties(builder: StateManager.Builder<Fluid, FluidState>) {
            super.appendProperties(builder)
            builder.add(LEVEL)
        }

        override fun getLevel(state: FluidState): Int { return state.get(LEVEL) }

        override fun isStill(state: FluidState): Boolean { return false }
    }

    class Still : ManaFluid() {
        override fun getLevel(state: FluidState): Int { return 8 }

        override fun isStill(state: FluidState): Boolean { return true }
    }
}