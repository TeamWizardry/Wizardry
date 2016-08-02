package com.teamwizardry.wizardry.common.world;

import com.teamwizardry.librarianlib.util.PosUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class WorldGeneratorCloud extends WorldGenerator {

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos position) {
		worldIn.setBlockState(position, Blocks.GRASS.getDefaultState(), 3);

		int x = position.getX();
		int y = ThreadLocalRandom.current().nextInt(50, 60);
		int z = position.getZ();
		int width = ThreadLocalRandom.current().nextInt(10, 20);
		int length = ThreadLocalRandom.current().nextInt(10, 20);
		int height = ThreadLocalRandom.current().nextInt(10, 20);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < length; j++) {
				for (int k = 0; k < height; k++) {
					BlockPos pos = new BlockPos(x + i, y + k, z + k);
					if (PosUtils.hasNeighboringBlock(worldIn, pos, Blocks.GRASS, true, true)) {
						if (ThreadLocalRandom.current().nextInt(20) > 0) {
							worldIn.setBlockState(pos, Blocks.GRASS.getDefaultState(), 3);
						}
					}
				}
			}
		}
		return true;
	}
}
