package com.teamwizardry.wizardry.multiblock;

import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;

public class AbstractBlockState {

    public static IProperty<?>[] IGNORE = new IProperty[]{
            BlockSlab.HALF,
            BlockStairs.SHAPE,
            BlockStairs.FACING,
            BlockPane.EAST, BlockPane.WEST, BlockPane.NORTH, BlockPane.SOUTH,
            BlockRedstoneWire.EAST, BlockRedstoneWire.WEST, BlockRedstoneWire.NORTH, BlockRedstoneWire.SOUTH, BlockRedstoneWire.POWER,
            BlockRedstoneComparator.FACING, BlockRedstoneComparator.MODE, BlockRedstoneComparator.POWERED,
            BlockRedstoneRepeater.FACING, BlockRedstoneRepeater.DELAY, BlockRedstoneRepeater.LOCKED
    };

}
