package com.teamwizardry.wizardry.common.block

import com.teamwizardry.wizardry.Wizardry
import net.minecraft.block.*
import net.minecraft.item.ItemStack
import net.minecraft.stat.Stats
import net.minecraft.state.property.Properties
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
class BlockWorktable(settings: Settings?) : HorizontalFacingBlock(settings), Waterloggable {
    enum class WorktablePart(private override val name: String) : StringIdentifiable {
        LEFT("left"), RIGHT("right");

        override fun toString(): String {
            return name
        }

        override fun asString(): String {
            return name
        }
    }

    override fun afterBreak(
        world: World, player: PlayerEntity, pos: BlockPos, state: BlockState,
        te: BlockEntity?, stack: ItemStack
    ) {
        super.afterBreak(world, player, pos, Blocks.AIR.defaultState, te, stack)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        val worktablePart = state.get<WorktablePart>(PART)
        val blockpos = pos.offset(getDirectionToOther(worktablePart, state.get(FACING)))
        val blockstate: BlockState = world.getBlockState(blockpos)
        if (blockstate.block === this && blockstate.get<WorktablePart>(PART) != worktablePart) {
            world.setBlockState(blockpos, Blocks.AIR.defaultState, NOTIFY_ALL or SKIP_DROPS)
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockpos, getRawIdFromState(blockstate))
            if (!world.isClient && !player.isCreative()) {
                val itemstack: ItemStack = player.getMainHandStack()
                dropStacks(state, world, pos, null, player, itemstack)
                dropStacks(blockstate, world, blockpos, null, player, itemstack)
            }
            player.increaseStat(Stats.MINED.getOrCreateStat(this), 1)
        }
        super.onBreak(world, pos, state, player)
    }

    fun getStateForPlacement(context: ItemPlacementContext): BlockState? {
        val world: World = context.getWorld()
        val direction: Direction = context.getPlayerFacing().rotateYClockwise()
        val blockpos: BlockPos = context.getBlockPos()
        val blockpos1 = blockpos.offset(direction)
        val flag = world.getFluidState(blockpos).getFluid() === Fluids.WATER
        return if (context.getWorld().getBlockState(blockpos1).canReplace(context)) this.defaultState
            .with(FACING, direction)
            .with(WATERLOGGED, flag) else null
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, PART, WATERLOGGED)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(WATERLOGGED)) Fluids.WATER.getStill(false) else super.getFluidState(state)
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.onPlaced(world, pos, state, placer, stack)
        if (!world.isClient) {
            val blockpos = pos.offset(state.get(FACING))
            world.setBlockState(blockpos, state.with(PART, WorktablePart.RIGHT), 3)
            world.updateNeighbors(pos, Blocks.AIR)
            state.updateNeighbors(world, pos, NOTIFY_ALL)
        }
    }

    override fun getPistonBehavior(state: BlockState): PistonBehavior {
        return PistonBehavior.IGNORE // Don't want dupes
    }

    override fun onUse(
        state: BlockState, world: World,
        pos: BlockPos, player: PlayerEntity,
        hand: Hand, hit: BlockHitResult
    ): ActionResult {
        if (world.isClient) Wizardry.PROXY!!.openWorktableGui()
        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType): Boolean {
        return false
    }

    companion object {
        val PART: EnumProperty<WorktablePart> = EnumProperty.of<WorktablePart>("part", WorktablePart::class.java)
        val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
        private fun getDirectionToOther(part: WorktablePart, facing: Direction): Direction {
            return if (part == WorktablePart.LEFT) facing else facing.opposite
        }
    }

    init {
        this.defaultState = this.stateManager.defaultState
            .with(PART, WorktablePart.LEFT)
            .with(FACING, Direction.NORTH)
            .with(WATERLOGGED, java.lang.Boolean.FALSE)
    }
}