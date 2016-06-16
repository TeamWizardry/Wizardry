package me.lordsaad.wizardry.tileentities;

import me.lordsaad.wizardry.ModItems;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.items.ItemPearl;
import me.lordsaad.wizardry.particles.SparkleFX;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 6/10/2016.
 */
public class TileCraftingPlate extends TileEntity implements ITickable {

    private ArrayList<ItemStack> inventory = new ArrayList<>();
    private boolean structureComplete = false;
    private boolean crafting = false, finishedCrafting = false;
    private int craftingProgress = 0, craftingTime = 200;
    private ItemStack pearl;

    public boolean isStructureComplete() {
        return structureComplete;
    }

    public void setStructureComplete(boolean structureComplete) {
        this.structureComplete = structureComplete;
    }

    public ItemStack getPearl() {
        return pearl;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        structureComplete = compound.getBoolean("structureComplete");
        inventory = new ArrayList<>();
        if (compound.hasKey("inventory")) {
            NBTTagList list = compound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++)
                inventory.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
        }
        if (compound.hasKey("pearl"))
            pearl = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("pearl"));
        if (compound.hasKey("crafting"))
            crafting = compound.getBoolean("crafting");
        if (compound.hasKey("finishedCrafting"))
            finishedCrafting = compound.getBoolean("finishedCrafting");
        if (compound.hasKey("craftingProgress"))
            craftingProgress = compound.getInteger("craftingProgress");
        if (compound.hasKey("craftingTime"))
            craftingProgress = compound.getInteger("craftingTime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("structureComplete", structureComplete);

        if (inventory.size() > 0) {
            NBTTagList list = new NBTTagList();
            for (ItemStack anInventory : inventory)
                list.appendTag(anInventory.writeToNBT(new NBTTagCompound()));
            compound.setTag("inventory", list);
        }
        if (pearl != null) compound.setTag("pearl", pearl.writeToNBT(compound));
        compound.setBoolean("crafting", crafting);
        compound.setBoolean("finishedCrafting", finishedCrafting);
        compound.setInteger("craftingProgress", craftingProgress);
        compound.setInteger("craftingTime", craftingTime);
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

        IBlockState state = worldObj.getBlockState(pos);
        worldObj.notifyBlockUpdate(pos, state, state, 3);
    }

    public ArrayList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (isStructureComplete()) {
                List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 2, 1)));
                for (EntityItem item : items) {
                    inventory.add(item.getEntityItem());
                    worldObj.removeEntity(item);
                    if (item.getEntityItem().getItem() == ModItems.pearl) {
                        ItemPearl pearl = (ItemPearl) item.getEntityItem().getItem();
                        if (pearl.getPearlType(item.getEntityItem()).equals("mundane")) {
                            this.pearl = item.getEntityItem();
                            worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
                            crafting = true;
                            craftingTime = (inventory.size() - 1) * 100;
                            craftingProgress = 0;
                        }
                    }
                    markDirty();
                    worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);

                }

                for (int i = 0; i < 5; i++) {
                    SparkleFX ambient = Wizardry.proxy.spawnParticleSparkle(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5F, 0.5F, 30, 8, 8, 8);
                    ambient.jitter(8, 0.1, 0.1, 0.1);
                    ambient.randomDirection(0.2, 0.2, 0.2);

                    /*if (!inventory.isEmpty()) {
                        SparkleFX fog = Wizardry.proxy.spawnParticleSparkle(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1F, 1F, 30);
                        fog.randomDirection(0.5, 0, 0.5);
                        fog.setMotion(0, -0.5, 0);
                    }*/
                }

                if (craftingProgress < craftingTime && isCrafting()) {
                    craftingProgress++;

                } else {
                    craftingProgress = 0;
                    crafting = false;
                    finishedCrafting = true;
                    if (pearl != null) ((ItemPearl) pearl.getItem()).addSpellItems(pearl, inventory);
                }
            }
        }
    }

    public int getCraftingProgress() {
        return craftingProgress;
    }

    public boolean isCrafting() {
        return crafting;
    }
}