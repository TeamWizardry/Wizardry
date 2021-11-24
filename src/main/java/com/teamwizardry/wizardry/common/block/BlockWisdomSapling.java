package com.teamwizardry.wizardry.common.block;

import java.util.Random;

import org.jetbrains.annotations.Nullable;

import com.teamwizardry.wizardry.common.init.ModBlocks;

import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class BlockWisdomSapling extends SaplingBlock {
    public BlockWisdomSapling(SaplingGenerator generator, Settings settings) {
        super(generator, settings);
    }
    
    public static class WisdomSaplingGenerator extends SaplingGenerator {
        
        private TreeFeatureConfig config;
        
        public TreeFeatureConfig getTreeFeatureConfig() {
            if (config == null)
                config = new TreeFeatureConfig.Builder(
                        new SimpleBlockStateProvider(ModBlocks.wisdomLog.getDefaultState()),
                        new StraightTrunkPlacer(5, 2, 0),
                        new SimpleBlockStateProvider(ModBlocks.wisdomLeaves.getDefaultState()),
                        new SimpleBlockStateProvider(ModBlocks.wisdomSapling.getDefaultState()),
                        new BlobFoliagePlacer(ConstantIntProvider.create(5), ConstantIntProvider.ZERO, 3),
                        new TwoLayersFeatureSize(1,0,1))
                        .build();    
            return config;
        }
        
        @Nullable
        @Override
        protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean hasNearbyFlora) {
            return Feature.TREE.configure(getTreeFeatureConfig());
        }
    }
}
