package com.teamwizardry.wizardry.common.world;

import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

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
