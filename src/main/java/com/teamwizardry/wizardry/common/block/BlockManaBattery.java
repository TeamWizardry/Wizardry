package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.wizardry.api.block.IManaNode;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.Nullable;


/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class BlockManaBattery extends ContainerBlock implements IWaterLoggable, IManaNode {
	protected BlockManaBattery(Properties builder) {
		super(builder);
	}

	@Override
	public ManaNodeType getManaNodeType() {
		return ManaNodeType.SOURCE;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return null;
	}
}
