package com.teamwizardry.wizardry.common.block

import com.teamwizardry.wizardry.common.init.ModBlocks
import net.minecraft.block.AbstractBlock
import net.minecraft.world.gen.feature.Feature
import java.util.*

class BlockWisdomSapling(generator: SaplingGenerator?, settings: AbstractBlock.Settings?) :
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
                    BlobFoliagePlacer(ConstantIntProvider.create(5), ConstantIntProvider.ZERO, 3),
                    TwoLayersFeatureSize(1, 0, 1)
                )
                    .build()
                return config
            }

        protected override fun getTreeFeature(
            randomIn: Random,
            hasNearbyFlora: Boolean
        ): ConfiguredFeature<TreeFeatureConfig, *>? {
            return Feature.TREE.configure(treeFeatureConfig)
        }
    }
}