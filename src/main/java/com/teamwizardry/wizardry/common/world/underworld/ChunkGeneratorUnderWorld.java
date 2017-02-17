package com.teamwizardry.wizardry.common.world.underworld;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad44
 */
public class ChunkGeneratorUnderWorld implements IChunkGenerator {

	private World world;

	public ChunkGeneratorUnderWorld(World worldIn) {
		this.world = worldIn;
	}

	private void generateCloud(Set<BlockPos> poses, BlockPos center, float weight, Random seed) {
		if (poses.contains(center)) {
			return;
		}
		poses.add(center);

		if (weight > 1) {
			List<EnumFacing> directions = new ArrayList<>();
			Set<EnumFacing> horizontals = new HashSet<>();
			Collections.addAll(directions, EnumFacing.VALUES);
			Collections.addAll(horizontals, EnumFacing.HORIZONTALS);
			while (!directions.isEmpty()) {
				int i = seed.nextInt(directions.size());
				EnumFacing dir;
				if (horizontals.contains(directions.get(i))) {
					if (ThreadLocalRandom.current().nextBoolean()) dir = directions.get(i);
					else dir = directions.remove(i);
				} else dir = directions.remove(i);
				generateCloud(poses, center.offset(dir), weight - seed.nextFloat() - 1, seed);
			}
		}
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

	private void generate(int chunkX, int chunkZ, ChunkPrimer primer) {
		for (int cx = -1; cx <= 1; cx++) {
			for (int cz = -1; cz <= 1; cz++) {
				if (isChunkCenter(chunkX + cx, chunkZ + cz)) {
					Random rand = getRandomForChunk(chunkX + cx, chunkZ + cz);
					Set<BlockPos> poses = new HashSet<>();
					generateCloud(poses, new BlockPos(8 + cx * 16, 50, 8 + cz * 16), 20, rand);

					for (BlockPos pos : poses) {
						if (pos.getX() >= 0 && pos.getX() <= 15
								&& pos.getZ() >= 0 && pos.getZ() <= 15) {
							IBlockState block = ModBlocks.CLOUD.getDefaultState();
							primer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), block);
						}
					}

					for (BlockPos pos : poses) {
						if (pos.getX() >= 0 && pos.getX() <= 15
								&& pos.getZ() >= 0 && pos.getZ() <= 15) {
							if (primer.getBlockState(pos.getX(), pos.getY() - 1, pos.getZ()) != ModBlocks.CLOUD) {
								IBlockState block = ModBlocks.CLOUD.getDefaultState();
								primer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), block);
							}
						}
					}
				}
			}
		}
	}

	@NotNull
	@Override
	public Chunk provideChunk(int x, int z) {
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
	public boolean generateStructures(@NotNull Chunk chunkIn, int x, int z) {
		return false;
	}

	@NotNull
	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(@NotNull EnumCreatureType creatureType, @NotNull BlockPos pos) {
		ArrayList<Biome.SpawnListEntry> list = new ArrayList<>();
		list.add(new Biome.SpawnListEntry(EntityFairy.class, 1, 1, 3));
		return list;
	}

	@Nullable
	@Override
	public BlockPos getStrongholdGen(@NotNull World worldIn, @NotNull String structureName, @NotNull BlockPos position) {
		return null;
	}

	@Override
	public void recreateStructures(@NotNull Chunk chunkIn, int x, int z) {

	}
}
