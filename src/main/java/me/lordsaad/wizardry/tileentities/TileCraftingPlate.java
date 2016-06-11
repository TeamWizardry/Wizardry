package me.lordsaad.wizardry.tileentities;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.particles.SparkleFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

/**
 * Created by Saad on 6/10/2016.
 */
public class TileCraftingPlate extends TileEntity implements ITickable {

    private ItemStack[] inventory = new ItemStack[54];
    private boolean structureComplete = false;

    private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (stack1.getMetadata() == stack2.getMetadata() && (stack1.stackSize <= stack1.getMaxStackSize() && (stack1.stackSize + stack2.stackSize) <= stack2.getMaxStackSize() && ItemStack.areItemStackTagsEqual(stack1, stack2)));
    }

    public boolean isStructureComplete() {
        return structureComplete;
    }

    public void setStructureComplete(boolean structureComplete) {
        this.structureComplete = structureComplete;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        structureComplete = compound.getBoolean("structureComplete");
        NBTTagList nbttaglist = compound.getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot");

            if (j >= 0 && j < this.inventory.length) {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("structureComplete", structureComplete);

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                this.inventory[i].writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        compound.setTag("Items", nbttaglist);

        return compound;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 2, 1)));
            for (EntityItem item : items) {
                for (int i = 0; i < inventory.length; i++) {
                    if (inventory[i] == null) {
                        inventory[i] = item.getEntityItem();
                        worldObj.removeEntity(item);
                        break;
                    } else if (canCombine(item.getEntityItem(), inventory[i])) {
                        inventory[i] = new ItemStack(item.getEntityItem().getItem(), item.getEntityItem().stackSize + inventory[i].stackSize);
                        worldObj.removeEntity(item);
                        break;
                    }
                }
            }
        }

        for (EntityPlayer player : worldObj.playerEntities)
            for (ItemStack stack : inventory)
                if (stack != null)
                    player.addChatMessage(new TextComponentString(stack + ""));

        if (isStructureComplete()) {
            for (int i = 0; i < 5; i++) {
                SparkleFX fx = Wizardry.proxy.spawnParticleSparkle(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5F, 0.5F, 30, 8, 8, 8);
                fx.jitter(8, 0.1, 0.1, 0.1);
                fx.randomDirection(0.3, 0.3, 0.3);
            }
        }
    }
}
