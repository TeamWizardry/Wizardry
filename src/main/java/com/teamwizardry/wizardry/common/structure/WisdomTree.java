package com.teamwizardry.wizardry.common.structure;

import com.teamwizardry.wizardry.common.init.ModBlocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class WisdomTree extends Tree {
    public static final TreeFeatureConfig WISDOM_TREE_CONFIG = (new TreeFeatureConfig.Builder(
            new SimpleBlockStateProvider(ModBlocks.wisdomLog.get().getDefaultState()),
            new SimpleBlockStateProvider(ModBlocks.wisdomLeaves.get().getDefaultState()),
            new BlobFoliagePlacer(2, 0)))
            .baseHeight(5)
            .heightRandA(2)
            .foliageHeight(3)
            .ignoreVines()
            .setSapling((IPlantable) ModBlocks.wisdomSapling.get())
            .build();

    @Nullable
    @Override
    protected ConfiguredFeature<TreeFeatureConfig, ?> getTreeFeature(Random randomIn, boolean hasNearbyFlora) {
        return Feature.NORMAL_TREE.withConfiguration(WISDOM_TREE_CONFIG);
    }
}
