package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.lib.LibBlockStateProperties;
import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import com.teamwizardry.wizardry.common.tile.TileMagicWorktable;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class BlockMagicWorktable extends HorizontalBlock implements IWaterLoggable {
    public static final EnumProperty<WorktablePart> PART = LibBlockStateProperties.WORKTABLE_PART;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public enum WorktablePart implements IStringSerializable {
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
        public @NotNull String getName() {
            return this.name;
        }
    }

    public BlockMagicWorktable(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(PART, WorktablePart.LEFT)
                .with(HORIZONTAL_FACING, Direction.NORTH)
                .with(WATERLOGGED, Boolean.FALSE));
    }

    private static Direction getDirectionToOther(WorktablePart part, Direction facing) {
        return part == WorktablePart.LEFT ? facing : facing.getOpposite();
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
                                          BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        if (facing == getDirectionToOther(stateIn.get(PART), stateIn.get(HORIZONTAL_FACING))) {
            return facingState.getBlock() == this && facingState.get(PART) != stateIn.get(PART) ?
                    stateIn :
                    Blocks.AIR.getDefaultState();
        } else {
            return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state,
                             @javax.annotation.Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IWorld iworld = context.getWorld();
        Direction direction = context.getPlacementHorizontalFacing().rotateY();
        BlockPos blockpos = context.getPos();
        BlockPos blockpos1 = blockpos.offset(direction);
        boolean flag = iworld.getFluidState(blockpos).getFluid() == Fluids.WATER;
        return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ?
                this.getDefaultState()
                        .with(HORIZONTAL_FACING, direction)
                        .with(WATERLOGGED, flag) :
                null;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, PART, WATERLOGGED);
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
        if (!state.get(BlockStateProperties.WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {

            worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.TRUE), 3);
            worldIn.getPendingFluidTicks()
                    .scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
            return true;
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public IFluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.IGNORE;  // Don't want dupes
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state,
                                @javax.annotation.Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if (!worldIn.isRemote) {
            BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING));
            worldIn.setBlockState(blockpos, state.with(PART, WorktablePart.RIGHT), 3);
            worldIn.notifyNeighbors(pos, Blocks.AIR);
            state.updateNeighbors(worldIn, pos, 3);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, @NotNull World worldIn,
                                                      @NotNull BlockPos pos, @NotNull PlayerEntity player,
                                                      @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        if (worldIn.isRemote) Wizardry.PROXY.openWorktableGui();
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Nullable
    @Override
    public TileMagicWorktable createTileEntity(BlockState state, IBlockReader world) {
        return LibTileEntityType.MAGICIANS_WORKTABLE.create();
    }


    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
