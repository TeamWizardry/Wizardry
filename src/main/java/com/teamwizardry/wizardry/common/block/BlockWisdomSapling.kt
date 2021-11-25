package com.teamwizardry.wizardry.common.block

import com.teamwizardry.wizardry.common.init.ModBlocks
import net.minecraft.block.SaplingBlock
import net.minecraft.block.sapling.SaplingGenerator
import net.minecraft.util.math.intprovider.ConstantIntProvider
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.TreeFeatureConfig
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize
import net.minecraft.world.gen.foliage.BlobFoliagePlacer
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider
import net.minecraft.world.gen.trunk.StraightTrunkPlacer
import java.util.*

class BlockWisdomSapling(generator: SaplingGenerator?, settings: Settings?) :
    SaplingBlock(generator, settings) {
    open class WisdomSaplingGenerator : SaplingGenerator() {
        private var config: TreeFeatureConfig? = null
        private val treeFeatureConfig: TreeFeatureConfig?
            get() {
                if (config == null) config = TreeFeatureConfig.Builder(
                        SimpleBlockStateProvider(ModBlocks.wisdomLog.defaultState),
                        StraightTrunkPlacer(5, 2, 0),
                        SimpleBlockStateProvider(ModBlocks.wisdomLeaves.defaultState),
                        SimpleBlockStateProvider(ModBlocks.wisdomSapling.defaultState),
                        BlobFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.ZERO, 3),
                        TwoLayersFeatureSize(1, 0, 1)
                ).build()
                return config
            }

        protected override fun getTreeFeature(random: Random, hasNearbyFlora: Boolean): ConfiguredFeature<TreeFeatureConfig, *>? { return Feature.TREE.configure(treeFeatureConfig) }
    }
}