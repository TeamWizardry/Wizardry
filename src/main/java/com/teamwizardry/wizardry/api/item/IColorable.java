package com.teamwizardry.wizardry.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/7/2016.
 */
public interface IColorable {

    default void setDefaultColor(ItemStack stack, int min, int max) {
        Color color = new Color(ThreadLocalRandom.current().nextInt(min, max), ThreadLocalRandom.current().nextInt(min, max), ThreadLocalRandom.current().nextInt(min, max));
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("red", color.getRed());
        compound.setInteger("green", color.getGreen());
        compound.setInteger("blue", color.getBlue());
        compound.setBoolean("checkRed", false);
        compound.setBoolean("checkBlue", false);
        compound.setBoolean("checkGreen", false);
        stack.setTagCompound(compound);
    }

}
