package me.lordsaad.wizardry.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Saad on 6/12/2016.
 */
public class TileMagiciansWorktable extends TileEntity {

    public EnumFacing direction = EnumFacing.NORTH;
    public BlockPos connectedPos = BlockPos.ORIGIN;
    public boolean isSlave = false;

    public void setInitialTableParameters(EnumFacing direction, boolean isSlave, BlockPos connectedPos) {
        this.isSlave = isSlave;
        this.connectedPos = connectedPos;
        if (!isSlave) this.direction = direction;
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        isSlave = compound.getBoolean("isSlave");
        direction = EnumFacing.getFront(compound.getInteger("direction"));
        connectedPos = new BlockPos(compound.getInteger("x_coord"), compound.getInteger("y_coord"), compound.getInteger("z_coord"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("isSlave", isSlave);
        compound.setInteger("direction", direction.getIndex());
        compound.setInteger("x_coord", connectedPos.getX());
        compound.setInteger("y_coord", connectedPos.getY());
        compound.setInteger("z_coord", connectedPos.getZ());
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }
}
