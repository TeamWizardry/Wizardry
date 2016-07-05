package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Created by Saad on 5/7/2016.
 */
public class TilePedestal extends TileEntity implements ITickable {

    private ItemStack stack;
    private List<Vec3d> points;
    private boolean draw;
    private BlockPos connectedManaBattery;
    private int queue = 0;

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        markDirty();
        if (worldObj != null) {
            IBlockState state = worldObj.getBlockState(getPos());
            worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("stack"))
            stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"));
        else stack = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (stack != null) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            stack.writeToNBT(tagCompound);
            compound.setTag("stack", tagCompound);
        }
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        // Here we get the packet from the server and read it into our client side tile entity
        this.readFromNBT(packet.getNbtCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void update() {
        if (draw) {
            if (queue < points.size()) {
                Vec3d location = points.get(queue);
                SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(worldObj, location.xCoord, location.yCoord, location.zCoord, 0.8F, 0.5F, 50, false);
                fizz.setMotion(0, 0.1, 0);
                fizz.setColor(0, 0, 0);
                fizz.jitter(20, 0.05, 0.05, 0.05);
                // fizz.randomlyOscillateColor(true, true, true);
                queue++;
            } else {
                queue = 0;
                draw = false;
            }
        }
    }

    public List<Vec3d> getPoints() {
        return points;
    }

    public void setPoints(List<Vec3d> points) {
        this.points = points;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public BlockPos getConnectedManaBattery() {
        return connectedManaBattery;
    }

    public void setConnectedManaBattery(BlockPos connectedManaBattery) {
        this.connectedManaBattery = connectedManaBattery;
    }
}
