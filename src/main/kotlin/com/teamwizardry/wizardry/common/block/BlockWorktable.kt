package com.teamwizardry.wizardry.common.block

import com.teamwizardry.wizardry.PROXY
import com.teamwizardry.wizardry.Wizardry
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.stat.Stats
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldEvents

class BlockWorktable(settings: Settings?) : HorizontalFacingBlock(settings), Waterloggable {
    enum class WorktablePart(val direction: String) : StringIdentifiable {
        LEFT("left"), RIGHT("right");

        override fun toString(): String { return direction }

        override fun asString(): String { return direction }
    }

    override fun afterBreak(world: World, player: PlayerEntity, pos: BlockPos, state: BlockState, te: BlockEntity?, stack: ItemStack) {
        super.afterBreak(world, player, pos, Blocks.AIR.defaultState, te, stack)
    }

    init {
        this.defaultState = this.defaultState.with(PART, WorktablePart.LEFT).with(FACING, Direction.NORTH).with(WATERLOGGED, java.lang.Boolean.FALSE)
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        val worktablePart = state.get(PART)
        val blockPos = pos.offset(getDirectionToOther(worktablePart, state.get(FACING)))
        val blockState: BlockState = world.getBlockState(blockPos)
        if (blockState.block === this && blockState.get(PART) != worktablePart) {
            world.setBlockState(blockPos, Blocks.AIR.defaultState, NOTIFY_ALL or SKIP_DROPS)
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, getRawIdFromState(blockState))
            if (!world.isClient && !player.isCreative) {
                val itemStack: ItemStack = player.mainHandStack
                dropStacks(state, world, pos, null, player, itemStack)
                dropStacks(blockState, world, blockPos, null, player, itemStack)
            }
            player.increaseStat(Stats.MINED.getOrCreateStat(this), 1)
        }
        super.onBreak(world, pos, state, player)
    }

    override fun getPlacementState(context: ItemPlacementContext): BlockState? {
        val world: World = context.world
        val direction: Direction = context.playerFacing.rotateYClockwise()
        val blockPos: BlockPos = context.blockPos
        val blockPos1 = blockPos.offset(direction)
        val flag = world.getFluidState(blockPos).fluid === Fluids.WATER
        return if (context.world.getBlockState(blockPos1).canReplace(context)) this.defaultState
            .with(FACING, direction)
            .with(WATERLOGGED, flag) else null
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING, PART, WATERLOGGED)
    }

    @Suppress("DEPRECATION")
    override fun getFluidState(state: BlockState): FluidState {
        return if (state.get(WATERLOGGED)) Fluids.WATER.getStill(false) else super.getFluidState(state)
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.onPlaced(world, pos, state, placer, stack)
        if (!world.isClient) {
            val blockPos = pos.offset(state.get(FACING))
            world.setBlockState(blockPos, state.with(PART, WorktablePart.RIGHT), 3)
            world.updateNeighbors(pos, Blocks.AIR)
            state.updateNeighbors(world, pos, NOTIFY_ALL)
        }
    }

    override fun getPistonBehavior(state: BlockState): PistonBehavior {
        return PistonBehavior.IGNORE // Don't want dupes
    }

    @Suppress("DEPRECATION")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (world.isClient) PROXY!!.openWorktableGui()
        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun getRenderType(state: BlockState): BlockRenderType { return BlockRenderType.MODEL }

    override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType): Boolean { return false }

    companion object {
        val PART: EnumProperty<WorktablePart> = EnumProperty.of("part", WorktablePart::class.java)
        val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
        private fun getDirectionToOther(part: WorktablePart, facing: Direction): Direction {
            return if (part == WorktablePart.LEFT) facing else facing.opposite
        }
    }


}