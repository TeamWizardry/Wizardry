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
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Demoniaque44
 */
public class ChunkGeneratorTorikki implements IChunkGenerator {
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState DIRT = Blocks.DIRT.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
	//protected static final IBlockState TOR_CRYSTAL = figure out how to do this.getDefaultState();
	private NoiseGeneratorPerlin noise;
	private NoiseGeneratorOctaves lperlinNoise1;
	private NoiseGeneratorOctaves lperlinNoise2;
	private World world;
	private final Random rand;
	public ChunkGeneratorTorikki(World worldIn) {

		this.world = worldIn;
		this.rand= new Random(world.getSeed());
		this.noise = new NoiseGeneratorPerlin(RandUtil.random,4);
		this.lperlinNoise1 = new NoiseGeneratorOctaves(this.rand, 16);
		this.lperlinNoise2 = new NoiseGeneratorOctaves(this.rand, 16);

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
		//go through x
		for (int locX = 0; locX < 16; locX++) {
			//go through y (irrelevant of chunk location)
			for (int locY = 0; locY < 256; locY++) {
				//and now for z
				for (int locZ = 0; locZ < 16; locZ++) {
					if ((locY <= 64 && locY > 1) || (locY > 192 && locY < 255)) {
						primer.setBlockState(locX, locY, locZ, DIRT);
					} else if (locY == 1 || locY == 255) {
						primer.setBlockState(locX, locY, locZ, BEDROCK);
					}
				}
				//if the noise <1, air, else torikki Grass?
			}
		}
		//mirroring!
		for (int locX = 0; locX < 16; locX++) {
			for (int locY = 65; locY < 128; locY++) {
				for (int locZ = 0; locZ < 16; locZ++) {
					if(.5 <noise.getValue(chunkZ, chunkX)){
						primer.setBlockState(locX,locY,locZ,DIRT);
					}
				}
			}
		}
	}
	@Nonnull
	@Override
	public Chunk generateChunk(int x, int z) {
		this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
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
