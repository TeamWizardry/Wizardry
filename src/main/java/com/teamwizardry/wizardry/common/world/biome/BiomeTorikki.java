package com.teamwizardry.wizardry.common.world.biome;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.common.world.trickery.TorikkiIceSpike;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by Tide
 * Based on BiomeUnderWorld
 * Unfinished, generation is not done yet
 * i havent gotten around to it
 */
public class BiomeTorikki extends Biome {
    private final TorikkiIceSpike iceSpike = new TorikkiIceSpike();
    public BiomeTorikki(BiomeProperties properties) {
        super(properties);
        properties.setRainDisabled();

        this.topBlock = Blocks.AIR.getDefaultState();
        this.fillerBlock = Blocks.AIR.getDefaultState();
        spawnableCreatureList.clear();
        spawnableWaterCreatureList.clear();
        spawnableMonsterList.clear();
        spawnableCaveCreatureList.clear();
        modSpawnableLists.clear();
    }

    @Override
    public void decorate(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos) {
        for (int i = 0; i < 3; ++i) {
            int j = rand.nextInt(16) + 8;
            int k = rand.nextInt(16) + 8;
            this.iceSpike.generate(worldIn, rand, worldIn.getHeight(pos.add(j, 0, k)));
        }
    }

    @SubscribeEvent
    public void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
        /*useless rn
        ill bring it back when i need it
        if (event.player.world.isRemote) return;
        if (event.player.world.provider.getDimensionType() != Wizardry.torikki) return;
        if (!event.player.world.getGameRules().getBoolean("doMobSpawning"))return;
        */
    }

    private int getEntityCount(Class<? extends Entity> entity, BlockPos pos, World world, int range) {
        List<Entity> entities = world.getEntitiesWithinAABB(entity, new AxisAlignedBB(pos).grow(range));
        return entities.size();
    }
}
