package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IManaAcceptor;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.init.ModItems;
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
    private BlockPos linkedBlock;
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
        if (compound.hasKey("stack")) stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"));
        else stack = null;

        int x = 0, y = 0, z = 0;
        if (compound.hasKey("link_x")) x = compound.getInteger("link_x");
        if (compound.hasKey("link_y")) y = compound.getInteger("link_y");
        if (compound.hasKey("link_z")) z = compound.getInteger("link_z");
        linkedBlock = new BlockPos(x, y, z);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (stack != null) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            stack.writeToNBT(tagCompound);
            compound.setTag("stack", tagCompound);
        }
        if (linkedBlock != null) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setInteger("link_x", pos.getX());
            tagCompound.setInteger("link_y", pos.getY());
            tagCompound.setInteger("link_z", pos.getZ());
            compound.setTag("linkedBlock", tagCompound);
        }
        return compound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
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
        if (linkedBlock != null && stack != null) {
            if (queue < points.size()) {
                Vec3d location = points.get(queue);
                SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(worldObj, location.xCoord, location.yCoord, location.zCoord, 0.8F, 0.5F, 30, false);
                fizz.setMotion(0, 0, 0);
                fizz.jitter(20, 0.05, 0.05, 0.05);
                queue++;
            } else {
                queue = 0;
                draw = false;
                points.clear();
            }
        }

        if (linkedBlock == null && stack != null) {
            if (stack.getItem() != ModItems.PEARL_MANA) return;
            if (!stack.hasTagCompound()) return;
            NBTTagCompound compound = stack.getTagCompound();
            int x = 0, y = 0, z = 0;
            if (compound.hasKey("link_x")) x = compound.getInteger("link_x");
            if (compound.hasKey("link_y")) y = compound.getInteger("link_y");
            if (compound.hasKey("link_z")) z = compound.getInteger("link_z");
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState block = worldObj.getBlockState(pos);
            if (block instanceof IManaAcceptor) linkedBlock = pos;
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

    public BlockPos getLinkedBlock() {
        return linkedBlock;
    }

    public void setLinkedBlock(BlockPos pos) {
        this.linkedBlock = pos;
    }

}
