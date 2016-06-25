package com.teamwizardry.wizardry.world;

import com.teamwizardry.wizardry.fluid.FluidBlockMana;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderOverworld;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class GenHandler implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        BlockPos pos = new BlockPos(chunkX, 1, chunkZ);
        if (chunkGenerator instanceof ChunkProviderOverworld) {
            this.generateOverworld(world, random, chunkX, chunkZ);
        }
    }

    public void generateOverworld(World world, Random rand, int x, int z) {
        int x1 = x;
        int y1 = 0;
        int z1 = z;
        BlockPos pos = new BlockPos(x1 * 16, 0, z1 * 16);
        Chunk chunk = world.getChunkFromBlockCoords(pos);
        BiomeProvider biomeProvider = world.getBiomeProvider();
        Biome biome = chunk.getBiome(pos, biomeProvider);

        generateMana(world, rand, x1, z1);
    }

    private void generateMana(World world, Random rand, int chunkX, int chunkZ) {
        WorldGenManaLake gen = new WorldGenManaLake(FluidBlockMana.instance);
        for (int i = 0; i < 1; i++) {
            int xRand = chunkX * 16 + rand.nextInt(16);
            int yRand = rand.nextInt(256);
            int zRand = chunkZ * 16 + rand.nextInt(16);
            BlockPos position = new BlockPos(xRand, yRand, zRand);
            gen.generate(world, rand, position);
        }
    }
}
