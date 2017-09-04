package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by Saad on 9/4/2016.
 */
public class BlockManaMagnet extends BlockMod {

	private static final AxisAlignedBB AABB_MANA_MAGNET = new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.5625, 0.9375);

	public BlockManaMagnet() {
		super("mana_magnet", Material.WOOD);
		setHardness(2.0f);
		setResistance(15.0f);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB_MANA_MAGNET;
	}
}
