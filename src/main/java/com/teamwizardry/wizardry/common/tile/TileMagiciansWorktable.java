package com.teamwizardry.wizardry.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Saad on 6/12/2016.
 */
public class TileMagiciansWorktable extends TileEntity {

	private BlockPos linkedTable;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int x = 0, y = 0, z = 0;
		if (compound.hasKey("x")) x = compound.getInteger("x");
		if (compound.hasKey("y")) y = compound.getInteger("y");
		if (compound.hasKey("z")) z = compound.getInteger("z");
		linkedTable = new BlockPos(x, y, z);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (linkedTable != null) {
			compound.setInteger("x", linkedTable.getX());
			compound.setInteger("y", linkedTable.getY());
			compound.setInteger("z", linkedTable.getZ());
		}
		return compound;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	public BlockPos getLinkedTable() {
		return linkedTable;
	}

	public void setLinkedTable(BlockPos linkedTable) {
		this.linkedTable = linkedTable;
	}
}
