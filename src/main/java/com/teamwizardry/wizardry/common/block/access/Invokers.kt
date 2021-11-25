package com.teamwizardry.wizardry.common.block.access

import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.DoorBlock
import net.minecraft.block.StairsBlock

/*****************
 * TEMPORARY CLASS
 *
 * Should be using invoker mixins (icky) or liblib foundation
 */
class Invokers {
    class DoorBlock(settings: AbstractBlock.Settings?) : net.minecraft.block.DoorBlock(settings)
    class StairsBlock(baseBlockState: BlockState?, settings: AbstractBlock.Settings?) : net.minecraft.block.StairsBlock(baseBlockState, settings)
}