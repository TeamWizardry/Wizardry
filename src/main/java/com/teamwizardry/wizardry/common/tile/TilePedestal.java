package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.wizardry.api.block.IManaSink;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Saad on 5/7/2016.
 */
public class TilePedestal extends TileEntity implements ITickable {

    private ItemStack manaPearl;
    private IBlockState state;

    public ItemStack getManaPearl() {
        return manaPearl;
    }

    public void setManaPearl(ItemStack manaPearl) {
        this.manaPearl = manaPearl;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("mana_pearl"))
            manaPearl = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("man_pearl"));
        else manaPearl = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (manaPearl != null) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            manaPearl.writeToNBT(tagCompound);
            compound.setTag("mana_pearl", tagCompound);
        }
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);
        readFromNBT(packet.getNbtCompound());

        state = worldObj.getBlockState(pos);
        worldObj.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public void update() {
        if (worldObj.isRemote) return;

        if (manaPearl == null) return;

        NBTTagCompound compound = manaPearl.getTagCompound();
        if (compound == null) return;
        if (!compound.hasKey("link_x") || !compound.hasKey("link_y") || !compound.hasKey("link_z")) return;

        BlockPos pos = new BlockPos(compound.getInteger("link_x"), compound.getInteger("link_y"), compound.getInteger("link_z"));
        IBlockState block = worldObj.getBlockState(pos);
        if (!(block.getBlock() instanceof IManaSink)) return;

        // TODO
    }
}
