package com.teamwizardry.wizardry.common.world;

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
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class ChunkGeneratorUnderWorld implements IChunkGenerator {

	private World world;

	public ChunkGeneratorUnderWorld(World worldIn) {
		this.world = worldIn;
	}

	public List<BlockPos> generateCloud(BlockPos center, int weight) {
		List<BlockPos> poses = new ArrayList<>();
		/*if (weight > 0) {
			if (ThreadLocalRandom.current().nextBoolean()) poses.addAll(generateCloud(center.south(), weight - 1));
			else  poses.addAll(generateCloud(center.south(), weight - 2));

			if (ThreadLocalRandom.current().nextBoolean()) poses.addAll(generateCloud(center.west(), weight - 1));
			else  poses.addAll(generateCloud(center.south(), weight - 2));

			if (ThreadLocalRandom.current().nextBoolean()) poses.addAll(generateCloud(center.north(), weight - 1));
			else  poses.addAll(generateCloud(center.south(), weight - 2));

			if (ThreadLocalRandom.current().nextBoolean()) poses.addAll(generateCloud(center.east(), weight - 1));
			else  poses.addAll(generateCloud(center.south(), weight - 2));
		}*/
		if(weight > 0) {
			poses.add(new BlockPos(center.south()));
			poses.addAll(generateCloud(center.south(), weight - 1));
		}
		return poses;
	}

	public void generate(int x, int z, ChunkPrimer primer) {
		int y = ThreadLocalRandom.current().nextInt(50, 60);

		for (BlockPos pos : generateCloud(new BlockPos(x, y, z), 10)) {
			primer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), Blocks.GRASS.getDefaultState());
			System.out.println("hi");
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
