package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.wizardry.api.block.IManaNode;
import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.Nullable;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class BlockManaBattery extends ContainerBlock implements IWaterLoggable, IManaNode {
	public BlockManaBattery(Properties builder) {
		super(builder);
	}

	@Override
	public ManaNodeType getManaNodeType() {
		return ManaNodeType.SOURCE;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return LibTileEntityType.MANA_BATTERY.get().create();
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return 15;
	}

	// TODO: Check if necessary, find replacement
//	@Override
//	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
//		return false;
//	}
}
