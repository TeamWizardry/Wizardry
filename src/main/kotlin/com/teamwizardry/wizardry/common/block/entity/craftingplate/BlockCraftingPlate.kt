package com.teamwizardry.wizardry.common.block.entity.craftingplate

import com.teamwizardry.wizardry.common.block.IManaNode
import com.teamwizardry.wizardry.common.init.ModBlocks
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class BlockCraftingPlate(settings: Settings?) : BlockWithEntity(settings), Waterloggable, IManaNode {
    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return SHAPE
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return checkType(
            type,
            ModBlocks.craftingPlateEntity
        ) { w: World, _: BlockPos, _: BlockState, e: BlockCraftingPlateEntity ->
            when (world.isClient) {
                true -> BlockCraftingPlateEntity.clientTick(w, e)
                false -> BlockCraftingPlateEntity.serverTick(w, e)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            if (hand == Hand.MAIN_HAND) {
                val heldItem: ItemStack = player.getStackInHand(hand)
                val blockEntity: BlockEntity? = world.getBlockEntity(pos)
                if (blockEntity is BlockCraftingPlateEntity) {
                    if (heldItem.isEmpty) player.setStackInHand(hand, blockEntity.removeItem())
                    else blockEntity.addItem(heldItem.split(1))
                }
            }
            return ActionResult.SUCCESS
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        val blockEntity: BlockEntity? = world.getBlockEntity(pos)
        if (blockEntity is BlockCraftingPlateEntity) blockEntity.onEntityCollision(entity)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockCraftingPlateEntity {
        return BlockCraftingPlateEntity(pos, state)
    }

    @Suppress("DEPRECATION", "DEPRECATION")
    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        val blockEntity: BlockEntity? = world.getBlockEntity(pos)
        if (blockEntity is BlockCraftingPlateEntity) ItemScatterer.spawn(world, pos, blockEntity.inventory)
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState? {
        return this.defaultState.with(WATERLOGGED, context.world.getFluidState(context.blockPos).fluid === Fluids.WATER)
    }

    @Suppress("DEPRECATION")
    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        newState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        posFrom: BlockPos
    ): BlockState {
        if (state.get(WATERLOGGED)) world.fluidTickScheduler.schedule(
            pos,
            Fluids.WATER,
            Fluids.WATER.getTickRate(world)
        )
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom)
    }

    @Suppress("DEPRECATION")
    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(WATERLOGGED)) Fluids.WATER.getStill(false) else super.getFluidState(state)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(WATERLOGGED)
    }

    override val manaNodeType: IManaNode.ManaNodeType
        get() = IManaNode.ManaNodeType.SINK

    companion object {
        val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
        private val SHAPE: VoxelShape = createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0)
    }

    init {
        this.defaultState = this.stateManager.defaultState.with(WATERLOGGED, java.lang.Boolean.FALSE)
    }
}