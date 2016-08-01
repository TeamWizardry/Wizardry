package com.teamwizardry.wizardry.common.world;

import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkProviderFlat;

/**
 * Created by LordSaad44
 */
public class ChunkProviderUnderWorld extends ChunkProviderFlat {

	private World world;

	public ChunkProviderUnderWorld(World worldIn, long seed, boolean generateStructures, String flatGeneratorSettings) {
		super(worldIn, seed, generateStructures, flatGeneratorSettings);
		this.world = worldIn;
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		Chunk chunk = new Chunk(world, new ChunkPrimer(), x, z);
		Biome[] biomes = world.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
		byte[] ids = chunk.getBiomeArray();

		for (int i = 0; i < ids.length; ++i) ids[i] = (byte) Biome.getIdForBiome(biomes[i]);

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public void populate(int x, int z) {

	}
}
