package me.lordsaad.wizardry.multiblock;

import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;

public class AbstractBlockState {

	public static IProperty<?>[] IGNORE = new IProperty[] {
		BlockSlab.HALF,
		BlockStairs.SHAPE,
		BlockStairs.FACING,
		BlockPane.EAST, BlockPane.WEST, BlockPane.NORTH, BlockPane.SOUTH,
		BlockRedstoneWire.EAST, BlockRedstoneWire.WEST, BlockRedstoneWire.NORTH, BlockRedstoneWire.SOUTH, BlockRedstoneWire.POWER,
		BlockRedstoneComparator.FACING, BlockRedstoneComparator.MODE, BlockRedstoneComparator.POWERED,
		BlockRedstoneRepeater.FACING, BlockRedstoneRepeater.DELAY, BlockRedstoneRepeater.LOCKED
	};
	
}
