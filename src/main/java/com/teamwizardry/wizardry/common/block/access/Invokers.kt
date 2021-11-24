package com.teamwizardry.wizardry.common.block.access;

import net.minecraft.block.BlockState;

/*****************
 * TEMPORARY CLASS
 *****************
 * Should be using invoker mixins (icky) or liblib foundation
 */
public class Invokers {
    public static class DoorBlock extends net.minecraft.block.DoorBlock { public DoorBlock(Settings settings) { super(settings); } }
    public static class StairsBlock extends net.minecraft.block.StairsBlock { public StairsBlock(BlockState baseBlockState, Settings settings) { super(baseBlockState, settings); } }
}

