package com.teamwizardry.wizardry.common.block.entity.manabattery

import com.teamwizardry.wizardry.capability.network.ManaNetwork
import com.teamwizardry.wizardry.common.block.IManaNode
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Waterloggable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

class BlockManaBattery(settings: Settings?) : BlockWithEntity(settings), Waterloggable, IManaNode {

    init {
        this.defaultState = this.stateManager.defaultState.with(Properties.WATERLOGGED, false)
    }

    @Suppress("DEPRECATION")
    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState, world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
        if (state.get(Properties.WATERLOGGED)) {
            world.fluidTickScheduler.schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState {
        super.placeManaNode(context)

        val fluidState: FluidState = context.world.getFluidState(context.blockPos)
        val bl = fluidState.fluid === Fluids.WATER
        return super.getPlacementState(context)!!.with(Properties.WATERLOGGED, bl) as BlockState
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Properties.WATERLOGGED)
    }

    @Suppress("DEPRECATION")
    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(Properties.WATERLOGGED)) Fluids.WATER.getStill(false) else super.getFluidState(state)
    }

    override val manaNodeType: IManaNode.ManaNodeType
        get() = IManaNode.ManaNodeType.SOURCE

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BlockManaBatteryEntity(pos, state)
    }

    // TODO: Check if necessary, find replacement
    //	@Override
    //	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
    //		return false;
    //	}
}