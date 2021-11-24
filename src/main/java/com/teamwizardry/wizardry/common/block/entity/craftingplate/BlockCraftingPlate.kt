package com.teamwizardry.wizardry.common.block.entity.craftingplate

import com.teamwizardry.wizardry.common.init.ModBlocks
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.state.property.Properties
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView

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
        return if (world.isClient) checkType(
            type,
            ModBlocks.craftingPlateEntity,
            BlockEntityTicker<BlockCraftingPlateEntity> { w: World?, p: BlockPos?, s: BlockState?, e: BlockCraftingPlateEntity? ->
                BlockCraftingPlateEntity.clientTick(
                    w,
                    e
                )
            }) else checkType(
            type,
            ModBlocks.craftingPlateEntity,
            BlockEntityTicker<BlockCraftingPlateEntity> { w: World?, p: BlockPos?, s: BlockState?, e: BlockCraftingPlateEntity ->
                BlockCraftingPlateEntity.serverTick(
                    world,
                    e
                )
            })
    }

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
                val blockEntity: BlockEntity = world.getBlockEntity(pos)
                if (blockEntity is BlockCraftingPlateEntity) {
                    val plate = blockEntity as BlockCraftingPlateEntity
                    if (heldItem.isEmpty) player.setStackInHand(
                        hand,
                        plate.removeItem()
                    ) else plate.addItem(heldItem.split(1))
                }
            }
            return ActionResult.SUCCESS
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        val blockEntity: BlockEntity = world.getBlockEntity(pos)
        if (blockEntity is BlockCraftingPlateEntity) (blockEntity as BlockCraftingPlateEntity).onEntityCollision(entity)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockCraftingPlateEntity {
        return BlockCraftingPlateEntity(pos, state)
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        val blockEntity: BlockEntity = world.getBlockEntity(pos)
        if (blockEntity is BlockCraftingPlateEntity) ItemScatterer.spawn(
            world,
            pos,
            (blockEntity as BlockCraftingPlateEntity).inventory
        )
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState? {
        return this.defaultState.with(
            WATERLOGGED,
            context.getWorld().getFluidState(context.getBlockPos()).getFluid() === Fluids.WATER
        )
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        newState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        posFrom: BlockPos
    ): BlockState {
        if (state.get(WATERLOGGED)) world.getFluidTickScheduler()
            .schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world))
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(WATERLOGGED)) Fluids.WATER.getStill(false) else super.getFluidState(state)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(WATERLOGGED)
    }

    val manaNodeType: ManaNodeType
        get() = ManaNodeType.SINK

    companion object {
        val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
        private val SHAPE: VoxelShape = createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0)
    }

    init {
        this.defaultState = this.stateManager.defaultState.with(WATERLOGGED, java.lang.Boolean.FALSE)
    }
}