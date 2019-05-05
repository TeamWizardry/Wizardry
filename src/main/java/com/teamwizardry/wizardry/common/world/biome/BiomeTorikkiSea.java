package com.teamwizardry.wizardry.common.world.biome;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;
/**
    Under Construction By @Tide
    DO NOT USE
    !!!!UNFINISHED!!!!
 */
public class BiomeTorikkiSea extends Biome {
    public BiomeTorikkiSea(BiomeProperties properties) {
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
    }

    @SubscribeEvent
    public void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
    }
    private int getEntityCount(Class<? extends Entity> entity, BlockPos pos, World world, int range) {
        List<Entity> entities = world.getEntitiesWithinAABB(entity, new AxisAlignedBB(pos).grow(range));
        return entities.size();
    }
}
