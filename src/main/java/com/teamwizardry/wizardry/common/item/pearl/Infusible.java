package com.teamwizardry.wizardry.common.item.pearl;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;

/**
 * Created by Saad on 6/30/2016.
 */
public abstract class Infusible extends Explodable {

    public PearlType getType(ItemStack stack) {
        if (stack.hasTagCompound())
            if (stack.getTagCompound().hasKey("type"))
                return PearlType.valueOf(stack.getTagCompound().getString("type").toUpperCase());
            else return PearlType.MUNDANE;
        else return PearlType.MUNDANE;
    }

    public void addSpellItems(ItemStack stack, ArrayList<ItemStack> items) {
        NBTTagCompound compound = new NBTTagCompound();
        if (items.size() > 0) {
            NBTTagList list = new NBTTagList();
            for (ItemStack anInventory : items)
                list.appendTag(anInventory.writeToNBT(new NBTTagCompound()));
            compound.setTag("inventory", list);
        }
        compound.setString("type", String.valueOf("infused"));
        stack.setTagCompound(compound);
    }
}
