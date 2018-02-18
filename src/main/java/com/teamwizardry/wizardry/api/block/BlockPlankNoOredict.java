package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockPlankNoOredict extends BlockMod {
	public BlockPlankNoOredict(String name) {
		super(name, Material.WOOD);
		setSoundType(SoundType.WOOD);
		setHardness(2);
		setResistance(5);
	}

	public String getHarvestTool(IBlockState state) {
		return "axe";
	}

	public boolean isToolEffective(String type, IBlockState state) {
		return type.equals("axe");
	}

	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		return 20;
	}

	public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		return 5;
	}
}
