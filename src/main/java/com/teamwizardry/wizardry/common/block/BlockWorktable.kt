package com.teamwizardry.wizardry.common.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.teamwizardry.wizardry.Wizardry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class BlockWorktable extends HorizontalFacingBlock implements Waterloggable {
    public static final EnumProperty<WorktablePart> PART = EnumProperty.of("part", BlockWorktable.WorktablePart.class);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public enum WorktablePart implements StringIdentifiable {
        LEFT("left"),
        RIGHT("right");

        private final String name;

        WorktablePart(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public @NotNull String asString() {
            return this.name;
        }
    }

    public BlockWorktable(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(PART, WorktablePart.LEFT)
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, Boolean.FALSE));
    }

    private static Direction getDirectionToOther(WorktablePart part, Direction facing) {
        return part == WorktablePart.LEFT ? facing : facing.getOpposite();
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
                             @Nullable BlockEntity te, ItemStack stack) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        WorktablePart worktablePart = state.get(PART);
        BlockPos blockpos = pos.offset(getDirectionToOther(worktablePart, state.get(FACING)));
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() == this && blockstate.get(PART) != worktablePart) {
            world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
            world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockpos, Block.getRawIdFromState(blockstate));
            if (!world.isClient && !player.isCreative()) {
                ItemStack itemstack = player.getMainHandStack();
                Block.dropStacks(state, world, pos, null, player, itemstack);
                Block.dropStacks(blockstate, world, blockpos, null, player, itemstack);
            }

            player.increaseStat(Stats.MINED.getOrCreateStat(this), 1);
        }

        super.onBreak(world, pos, state, player);
    }

    @Nullable
    public BlockState getStateForPlacement(ItemPlacementContext context) {
        World world = context.getWorld();
        Direction direction = context.getPlayerFacing().rotateYClockwise();
        BlockPos blockpos = context.getBlockPos();
        BlockPos blockpos1 = blockpos.offset(direction);
        boolean flag = world.getFluidState(blockpos).getFluid() == Fluids.WATER;
        return context.getWorld().getBlockState(blockpos1).canReplace(context) ?
                this.getDefaultState()
                        .with(FACING, direction)
                        .with(WATERLOGGED, flag) :
                null;
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);

        if (!world.isClient) {
            BlockPos blockpos = pos.offset(state.get(FACING));
            world.setBlockState(blockpos, state.with(PART, WorktablePart.RIGHT), 3);
            world.updateNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(world, pos, Block.NOTIFY_ALL);
        }
    }
    
    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.IGNORE; // Don't want dupes
    }

    @Override
    public @NotNull ActionResult onUse(@NotNull BlockState state, @NotNull World world,
                                                      @NotNull BlockPos pos, @NotNull PlayerEntity player,
                                                      @NotNull Hand hand, @NotNull BlockHitResult hit) {
        if (world.isClient) Wizardry.PROXY.openWorktableGui();
        return super.onUse(state, world, pos, player, hand, hit);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
