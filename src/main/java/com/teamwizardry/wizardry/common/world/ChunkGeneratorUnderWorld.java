package com.teamwizardry.wizardry.common.world;

import com.teamwizardry.librarianlib.util.PosUtils;
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


	public void generate(int x, int z, ChunkPrimer primer) {
		int y = ThreadLocalRandom.current().nextInt(50, 60);

		primer.setBlockState(x, y, z, Blocks.GRASS.getDefaultState());

		int width = ThreadLocalRandom.current().nextInt(10, 20);
		int length = ThreadLocalRandom.current().nextInt(10, 20);
		int height = ThreadLocalRandom.current().nextInt(10, 20);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < length; j++) {
				for (int k = 0; k < height; k++) {
					BlockPos pos = new BlockPos(x + i, y + k, z + k);
					if (PosUtils.hasNeighboringBlock(world, pos, Blocks.GRASS, true, true)) {
						if (ThreadLocalRandom.current().nextInt(20) > 0) {
							primer.setBlockState(x, y, z, Blocks.GRASS.getDefaultState());
						}
					}
				}
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
