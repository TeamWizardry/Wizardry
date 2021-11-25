package com.teamwizardry.wizardry.common.block.access

import net.minecraft.block.BlockState

/*****************
 * TEMPORARY CLASS
 *
 * Should be using invoker mixins (icky) or liblib foundation
 */
class Invokers {
    class DoorBlock(settings: Settings?) : net.minecraft.block.DoorBlock(settings)
    class StairsBlock(baseBlockState: BlockState?, settings: Settings?) : net.minecraft.block.StairsBlock(baseBlockState, settings)
}