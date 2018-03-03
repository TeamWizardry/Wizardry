package com.teamwizardry.wizardry.common.world;

import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;
import java.util.stream.IntStream;

public class GenHandler implements IWorldGenerator {

	private void generateMana(World world, Random rand, int x, int z) {
		for (int i = 0; i < 1; i++) {
			WorldGenManaLake gen = new WorldGenManaLake(ModFluids.MANA.getActualBlock());
			int xRand = x * 16 + rand.nextInt(16);
			int zRand = z * 16 + rand.nextInt(16);
			int yRand = world.getChunkFromChunkCoords(x, z).getLowestHeight();
//			int yRand = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
			yRand = RandUtil.nextInt(yRand - 1, yRand);
			BlockPos position = new BlockPos(xRand, yRand, zRand);
			gen.generate(world, rand, position);
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (ConfigValues.manaPoolRarity > 0)
			if (IntStream.of(ConfigValues.manaPoolDimWhitelist).boxed().anyMatch(dim -> dim == world.provider.getDimension()))
				generateMana(world, random, chunkX, chunkZ);
	}
}
