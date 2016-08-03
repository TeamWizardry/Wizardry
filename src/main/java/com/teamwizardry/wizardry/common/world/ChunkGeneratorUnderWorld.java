package com.teamwizardry.wizardry.common.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by LordSaad44
 */
public class ChunkGeneratorUnderWorld implements IChunkGenerator {

	private World world;

	public ChunkGeneratorUnderWorld(World worldIn) {
		this.world = worldIn;
	}

	public List<BlockPos> generateCloud(BlockPos center, int weight, float seed) {
		List<BlockPos> poses = new ArrayList<>();
		poses.add(new BlockPos(center));

		if (weight > 0) {
			if (seed < 0.5f) poses.addAll(generateCloud(center.south(), weight - 1, seed));
			else poses.addAll(generateCloud(center.south(), weight - 2, seed));

			if (seed < 0.5f) poses.addAll(generateCloud(center.north(), weight - 1, seed));
			else poses.addAll(generateCloud(center.north(), weight - 2, seed));

			if (seed < 0.5f) poses.addAll(generateCloud(center.east(), weight - 1, seed));
			else poses.addAll(generateCloud(center.east(), weight - 2, seed));

			if (seed < 0.5f) poses.addAll(generateCloud(center.west(), weight - 1, seed));
			else poses.addAll(generateCloud(center.west(), weight - 2, seed));
		}
		return poses;
	}

	public void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
		long s2 = (((chunkX >> 2) + world.getSeed() + 13) * 314) + (chunkZ >> 2) * 17L;
		Random rand = new Random(s2);
		rand.nextFloat();
		float seed = rand.nextFloat();

		List<BlockPos> poses = generateCloud(new BlockPos(chunkX, 50, chunkZ), 10, seed);

		for (BlockPos pos : poses) {
			if (seed < 0.5f) {
				int x = pos.getX() * 16;
				int y = pos.getY() * 16;
				int z = pos.getZ() * 16;
				IBlockState block = Blocks.STONE.getDefaultState();
				primer.setBlockState(x, y, z, block);
			}
		}
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		ChunkPrimer chunkprimer = new ChunkPrimer();

		generate(x, z, chunkprimer);

		Chunk chunk = new Chunk(world, chunkprimer, x, z);

		byte[] biomeArray = chunk.getBiomeArray();
		for (int i = 0; i < biomeArray.length; ++i) {
			biomeArray[i] = (byte) Biome.getIdForBiome(Biomes.PLAINS);
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public void populate(int x, int z) {

	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return Collections.emptyList();
	}

	@Nullable
	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {

	}
}
