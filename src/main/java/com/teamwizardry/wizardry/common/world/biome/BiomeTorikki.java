package com.teamwizardry.wizardry.common.world.biome;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by Demoniaque44
 */
public class BiomeTorikki extends Biome {

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

    }

    @SubscribeEvent
    public void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.world.isRemote) return;
        if (event.player.world.provider.getDimensionType() != Wizardry.torikki) return;
        if (!event.player.world.getGameRules().getBoolean("doMobSpawning")); //return;
    }

    private int getEntityCount(Class<? extends Entity> entity, BlockPos pos, World world, int range) {
        List<Entity> entities = world.getEntitiesWithinAABB(entity, new AxisAlignedBB(pos).grow(range));
        return entities.size();
    }
}
