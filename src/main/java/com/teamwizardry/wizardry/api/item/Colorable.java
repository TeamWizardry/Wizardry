package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.util.Color;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/7/2016.
 */
public interface Colorable {

    default void setDefaultColor(ItemStack stack, int min, int max) {
        Color color = new Color(ThreadLocalRandom.current().nextInt(min, max), ThreadLocalRandom.current().nextInt(min, max), ThreadLocalRandom.current().nextInt(min, max));
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("red", (int) color.r);
        compound.setInteger("green", (int) color.g);
        compound.setInteger("blue", (int) color.b);
        compound.setBoolean("checkRed", false);
        compound.setBoolean("checkBlue", false);
        compound.setBoolean("checkGreen", false);
        stack.setTagCompound(compound);
    }

    Color getColor(ItemStack stack);
}
