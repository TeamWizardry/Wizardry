package com.teamwizardry.wizardry.common.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

import javax.annotation.Nullable;

/**
 * Created by LordSaad44
 */
public class ChunkProviderUnderWorld implements IChunkProvider {

    @Nullable
    @Override
    public Chunk getLoadedChunk(int x, int z) {
        return null;
    }

    @Override
    public Chunk provideChunk(int x, int z) {

        ChunkPrimer primer = new ChunkPrimer();
        Block cloud = Blocks.GRASS;

        for (int y = 50; y < 100; y++) {
            for (int xi = 0; xi < 16; xi++) {
                for (int zi = 0; zi < 16; zi++) {
                }
            }
        }

        return null;
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public String makeString() {
        return null;
    }
}
