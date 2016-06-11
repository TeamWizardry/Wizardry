package me.lordsaad.wizardry.tileentities;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.particles.SparkleFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
        inventory = new ArrayList<>();
        if (compound.hasKey("inventory")) {
            NBTTagList list = compound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++)
                inventory.add(ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
        }
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
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
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
                    markDirty();
                }

                for (int i = 0; i < 5; i++) {
                    SparkleFX ambient = Wizardry.proxy.spawnParticleSparkle(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.5F, 0.5F, 30, 8, 8, 8);
                    ambient.jitter(8, 0.1, 0.1, 0.1);
                    ambient.randomDirection(0.2, 0.2, 0.2);

                /*SparkleFX fog = Wizardry.proxy.spawnParticleSparkle(worldObj, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1F, 1F, 30);
                fog.randomDirection(0.5, 0, 0.5);
                fog.setMotion(0, -0.5, 0);*/
                }
            }
        }
    }
}