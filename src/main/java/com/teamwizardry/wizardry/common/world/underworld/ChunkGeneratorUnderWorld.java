package com.teamwizardry.wizardry.common.world.underworld;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.common.block.BlockCloud;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.init.ModBlocks;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

/**
 * Created by LordSaad44
 */
public class ChunkGeneratorUnderWorld implements IChunkGenerator {

	private static final int UPPER_LEVEL = 100;
	private static final int LOWER_LEVEL = 105;
	private static final double UPPER_X_SCALE = 32.0;
	private static final double UPPER_Y_SCALE = 1.0 / 2.0;
	private static final double UPPER_Z_SCALE = 32.0;
	private static final double LOWER_X_SCALE = 16.0;
	private static final double LOWER_Y_SCALE = 2.5;
	private static final double LOWER_Z_SCALE = 16.0;
	
	private NoiseGeneratorPerlin upper;
	private NoiseGeneratorPerlin lower;
	
	private World world;

	public ChunkGeneratorUnderWorld(World worldIn) {
		this.world = worldIn;
		upper = new NoiseGeneratorPerlin(RandUtil.random, 4);
		lower = new NoiseGeneratorPerlin(RandUtil.random, 4);
	}
	
	public ChunkGeneratorUnderWorld(World world, long seed)
	{
		this.world = world;
		RandUtilSeed rand = new RandUtilSeed(seed);
		upper = new NoiseGeneratorPerlin(rand.random, 4);
		lower = new NoiseGeneratorPerlin(rand.random, 4);
	}

	private void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
		double[][] upperValues = new double[16][16];
		double[][] lowerValues = new double[16][16];
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				upperValues[x][z] = upper.getValue((chunkX * 16 + x) / UPPER_X_SCALE, (chunkZ * 16 + z) / UPPER_Z_SCALE);
				lowerValues[x][z] = lower.getValue((chunkX * 16 + x) / LOWER_X_SCALE, (chunkZ * 16 + z) / LOWER_Z_SCALE);
			}
		}
		
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				int minY = (int) (lowerValues[x][z] * LOWER_Y_SCALE + LOWER_LEVEL);
				int maxY = (int) (upperValues[x][z] * UPPER_Y_SCALE + UPPER_LEVEL);
				for (int y = minY; y <= maxY; y++)
				{
					if (y == minY)
						primer.setBlockState(x, y, z, ModBlocks.CLOUD.getDefaultState().withProperty(BlockCloud.HAS_LIGHT_VALUE, true));
					else
						primer.setBlockState(x, y, z, ModBlocks.CLOUD.getDefaultState());
				}
			}
		}
	}

	@Nonnull
	@Override
	public Chunk generateChunk(int x, int z) {
		ChunkPrimer chunkprimer = new ChunkPrimer();

		generate(x, z, chunkprimer);

		Chunk chunk = new Chunk(world, chunkprimer, x, z);

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public void populate(int x, int z) {
		if (x / 16 == 0 && z / 16 == 0) {
			for (int i = -3; i < 3; i++)
				for (int j = -3; j < 3; j++) {
					world.setBlockState(new BlockPos(i, 100, j), Blocks.OBSIDIAN.getDefaultState());
				}
			world.setBlockState(new BlockPos(-1, 101, -1), Blocks.TORCH.getDefaultState());
			world.setBlockState(new BlockPos(0, 101, -1), Blocks.TORCH.getDefaultState());
			world.setBlockState(new BlockPos(-1, 101, 0), Blocks.TORCH.getDefaultState());
			world.setBlockState(new BlockPos(0, 101, 0), Blocks.TORCH.getDefaultState());
		}
	}

	@Override
	public boolean generateStructures(@Nonnull Chunk chunkIn, int x, int z) {
		return false;
	}

	@Nonnull
	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(@Nonnull EnumCreatureType creatureType, @Nonnull BlockPos pos) {
		ArrayList<Biome.SpawnListEntry> list = new ArrayList<>();
		list.add(new Biome.SpawnListEntry(EntityFairy.class, 1, 1, 3));
		return list;
	}

	@Nullable
	@Override
	public BlockPos getNearestStructurePos(@Nonnull World worldIn, @Nonnull String structureName, @Nonnull BlockPos position, boolean findUnexplored) {
		return null;
	}

	@Override
	public void recreateStructures(@Nonnull Chunk chunkIn, int x, int z) {

	}

	@Override
	public boolean isInsideStructure(@Nonnull World worldIn, @Nonnull String structureName, @Nonnull BlockPos pos) {
		return false;
	}
}
