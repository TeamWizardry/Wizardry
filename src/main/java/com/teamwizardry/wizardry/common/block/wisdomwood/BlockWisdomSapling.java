package com.teamwizardry.wizardry.common.block.wisdomwood;

import com.teamwizardry.librarianlib.features.base.block.BlockModSapling;
import com.teamwizardry.wizardry.common.world.WorldGeneratorWisdomTree;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockWisdomSapling extends BlockModSapling {

	public BlockWisdomSapling() {
		super("wisdom_sapling");
	}

	@Override
	public void generateTree(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {
		WorldGeneratorWisdomTree tree = new WorldGeneratorWisdomTree(true);
		tree.generate(worldIn, rand, pos);
	}
}
