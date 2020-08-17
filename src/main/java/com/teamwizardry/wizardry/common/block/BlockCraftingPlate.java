package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.jetbrains.annotations.Nullable;

public class BlockCraftingPlate extends Block {

	public BlockCraftingPlate(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return LibTileEntityType.CRAFTING_PLATE.create();
	}
}
