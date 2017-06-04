package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModLog;
import com.teamwizardry.wizardry.common.world.WorldGeneratorWisdomTree;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodLog extends BlockModLog {

	public BlockWisdomWoodLog() {
		super("wisdom_wood_log");
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		WorldGeneratorWisdomTree tree = new WorldGeneratorWisdomTree(true);
		tree.generate(worldIn, worldIn.rand, pos);
	}
}
