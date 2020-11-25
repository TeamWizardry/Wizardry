package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.lib.LibBlockStateProperties;
import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import com.teamwizardry.wizardry.common.tile.TileMagicWorktable;
import net.minecraft.block.*;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class BlockMagicWorktable extends HorizontalBlock {
	public static final EnumProperty<WorktablePart> PART = LibBlockStateProperties.WORKTABLE_PART;

	private static final VoxelShape boundsN = Block.makeCuboidShape(0, 0, 0, 32, 16, -16);
	private static final VoxelShape boundsS = Block.makeCuboidShape(0, 0, 0, -32, 16, 16);
	private static final VoxelShape boundsE = Block.makeCuboidShape(0, 0, 0, 16, 16, 32);
	private static final VoxelShape boundsW = Block.makeCuboidShape(0, 0, 0, -16, 16, -32);

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
		this.setDefaultState(this.stateContainer.getBaseState().with(PART, WorktablePart.LEFT));
	}

	private Direction getDirectionFacing(IBlockReader reader, BlockPos pos) {
		BlockState state = reader.getBlockState(pos);
		return state.getBlock() instanceof HorizontalBlock ? state.get(HORIZONTAL_FACING) : null;
	}

	private VoxelShape getBounds(IBlockReader worldIn, BlockPos pos) {
		Direction dir = getDirectionFacing(worldIn, pos);

		if(dir == null) return null;
		else {
			switch (dir) {
				case NORTH:
					return boundsN;

				case SOUTH:
					return boundsS;

				case EAST:
					return boundsE;

				case WEST:
					return boundsW;

				default:
					return null;  // should never get here!
			}
		}
	}

	private static Direction getDirectionToOther(WorktablePart part, Direction facing) {
		return part == WorktablePart.LEFT ? facing : facing.getOpposite();
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (facing == getDirectionToOther(stateIn.get(PART), stateIn.get(HORIZONTAL_FACING))) {
			return facingState.getBlock() == this && facingState.get(PART) != stateIn.get(PART) ? stateIn : Blocks.AIR.getDefaultState();
		} else {
			return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		}
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @javax.annotation.Nullable TileEntity te, ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction direction = context.getPlacementHorizontalFacing().rotateY();
		BlockPos blockpos = context.getPos();
		BlockPos blockpos1 = blockpos.offset(direction);
		return context.getWorld().getBlockState(blockpos1).isReplaceable(context) ? this.getDefaultState().with(HORIZONTAL_FACING, direction) : null;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING, PART);
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.IGNORE;  // Don't want dupes
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @javax.annotation.Nullable LivingEntity placer, ItemStack stack) {
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
	public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
		if (worldIn.isRemote) Wizardry.PROXY.openWorktableGui();
		return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
	}

	@Nullable
	@Override
	public TileMagicWorktable createTileEntity(BlockState state, IBlockReader world) {
		return LibTileEntityType.MAGICIANS_WORKTABLE.create();
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}

	@Override
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}
}
