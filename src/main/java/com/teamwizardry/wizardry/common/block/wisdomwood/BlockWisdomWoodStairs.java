package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.core.common.OreDictionaryRegistrar;
import com.teamwizardry.librarianlib.features.base.block.BlockModStairs;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Created by Demoniaque.
 */
public class BlockWisdomWoodStairs extends BlockModStairs {

	public BlockWisdomWoodStairs() {
		super("wisdom_wood_stairs", ModBlocks.WISDOM_WOOD_PLANKS.getDefaultState());
		setSoundType(SoundType.WOOD);
		OreDictionaryRegistrar.registerOre("stairWood", this);
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
	public boolean isTopSolid(IBlockState state) {
		return state.getValue(HALF) == BlockStairs.EnumHalf.TOP;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		if (net.minecraftforge.common.ForgeModContainer.disableStairSlabCulling)
			return super.doesSideBlockRendering(state, world, pos, face);

		if (state.isOpaqueCube())
			return true;

		state = this.getActualState(state, world, pos);

		EnumHalf half = state.getValue(HALF);
		EnumFacing side = state.getValue(FACING);
		EnumShape shape = state.getValue(SHAPE);
		if (face == EnumFacing.UP) return half == EnumHalf.TOP;
		if (face == EnumFacing.DOWN) return half == EnumHalf.BOTTOM;
		if (shape == EnumShape.OUTER_LEFT || shape == EnumShape.OUTER_RIGHT) return false;
		if (face == side) return true;
		if (shape == EnumShape.INNER_LEFT && face.rotateY() == side) return true;
		return shape == EnumShape.INNER_RIGHT && face.rotateYCCW() == side;
	}
}
