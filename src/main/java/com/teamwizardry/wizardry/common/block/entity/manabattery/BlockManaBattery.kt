package com.teamwizardry.wizardry.common.block.entity.manabattery;

import org.jetbrains.annotations.Nullable;

import com.teamwizardry.wizardry.common.block.IManaNode;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class BlockManaBattery extends BlockWithEntity implements Waterloggable, IManaNode {
	public BlockManaBattery(Settings settings) {
		super(settings);
	}

	@Override
	public ManaNodeType getManaNodeType() {
		return ManaNodeType.SOURCE;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new BlockManaBatteryEntity(pos, state);
	}

	// TODO: Check if necessary, find replacement
//	@Override
//	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
//		return false;
//	}
}
