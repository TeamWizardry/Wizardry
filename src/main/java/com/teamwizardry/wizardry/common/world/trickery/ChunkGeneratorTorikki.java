package com.teamwizardry.wizardry.common.world.trickery;

import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Demoniaque44
 */
public class ChunkGeneratorTorikki implements IChunkGenerator {
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
	protected static final IBlockState TORIKKIGRASS = Blocks.GRASS.getDefaultState();
	//protected static final IBlockState TOR_CRYSTAL = figure out how to do this.getDefaultState();
	private NoiseGeneratorPerlin noise;
	private World world;
	private final Random rand;
	public ChunkGeneratorTorikki(World worldIn) {

		this.world = worldIn;
		noise = new NoiseGeneratorPerlin(RandUtil.random,4);

		rand = new Random(world.getSeed());

	}
	public ChunkGeneratorTorikki(World world, long seed) {
		this.world = world;
		rand = new Random(world.getSeed());
		noise = new NoiseGeneratorPerlin(rand, 4);

	}

	private boolean isChunkCenter(int chunkX, int chunkZ) {
		long s2 = ((chunkX + world.getSeed() + 337) * 947) + chunkZ * 719L;
		Random rand = new Random(s2);
		rand.nextFloat();
		return rand.nextFloat() < .3f;
	}

	private Random getRandomForChunk(int chunkX, int chunkZ) {
		long s2 = ((chunkX + world.getSeed() + 13) * 314) + chunkZ * 17L;
		Random rand = new Random(s2);
		rand.nextFloat();
		return rand;
	}

	/**
	 * ok so
	 * this is supposed to work by genning everything below 128
	 * and then mirroring it over y - 128
	 * y = y > 128 ? 256 - y : y is the gud code
	 * idk how to make it work
	 */
	//0-15 for local, 16*chunk + 0-15 for noise
	private void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
		//ok we doin this bois
		//go through x
		for(int locX = 0; locX <16; locX++){
			//go through y (irrelevant of chunk location)
			for(int locY = 0; locY <256; locY++){
				//and now for z
				for(int locZ = 0; locZ <16; locZ++){
					if (locY<=64||locY>192){primer.setBlockState(locX,locY,locZ,TORIKKIGRASS);}

				}
			}
		}
	}
	//this is BO'P code that im using to figure out how ot MAKE THIS FUCKING WORK
	/*
	public Chunk generateChunk(int chunkX, int chunkZ) {
		this.rand.setSeed((long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);

		// create the primer
		ChunkPrimer chunkprimer = new ChunkPrimer();

		// start off by adding the basic terrain shape with air netherrack and lava blocks
		this.setChunkLavaNetherrack(chunkX, chunkZ, chunkprimer);

		this.buildSurfaces(chunkX, chunkZ, chunkprimer);
		this.genNetherCaves.generate(this.world, chunkX, chunkZ, chunkprimer);

		if (this.generateStructures) {
			this.genNetherBridge.generate(this.world, chunkX, chunkZ, chunkprimer);
		}

		Biome[] biomes = this.world.getBiomeProvider().getBiomes(null, chunkX * 16, chunkZ * 16, 16, 16);
		this.replaceBlocksForBiome(chunkX, chunkZ, chunkprimer, biomes);

		Chunk chunk = new Chunk(this.world, chunkprimer, chunkX, chunkZ);
		byte[] chunkBiomes = chunk.getBiomeArray();

		for (int i = 0; i < chunkBiomes.length; ++i) {
			chunkBiomes[i] = (byte) Biome.getIdForBiome(biomes[i]);
		}

		chunk.resetRelightChecks();
		return chunk;
	}
*/
	@Nonnull
	@Override
	public Chunk generateChunk(int x, int z) {
		ChunkPrimer chunkprimer = new ChunkPrimer();

		generate(x, z, chunkprimer);

		Chunk chunk = new Chunk(world, chunkprimer, x, z);

		byte[] biomeArray = chunk.getBiomeArray();
		for (int i = 0; i < biomeArray.length; ++i) {
			biomeArray[i] = (byte) 42;
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public void populate(int x, int z) {
		if (x / 16 == 0 && z / 16 == 0) {
			for (int i = -3; i < 3; i++)
				for (int j = -3; j < 3; j++) {
					world.setBlockState(new BlockPos(i, 50, j), Blocks.OBSIDIAN.getDefaultState());
				}
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
