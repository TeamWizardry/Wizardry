package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

/**
 * Created by Saad on 8/27/2016.
 */
public class BlockCloud extends BlockMod {

	public BlockCloud() {
		super("cloud", Material.CLOTH);
		setHardness(0.5f);
	}

	@Override
	public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
		return canProduceLight(world, pos) ? 15 : 0;
	}

	public boolean canProduceLight(IBlockAccess world, BlockPos pos) {
		for (int i = pos.getY(); i > 0; i--)
			if (!world.isAirBlock(pos.down(i))) return false;

		for (int i = pos.getY(); i < 255; i++)
			if (!world.isAirBlock(pos.up(i))) return true;

		return true;
	}
}
