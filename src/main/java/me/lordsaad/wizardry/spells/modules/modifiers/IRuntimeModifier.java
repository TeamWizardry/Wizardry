package me.lordsaad.wizardry.spells.modules.modifiers;

import net.minecraft.nbt.NBTTagCompound;

public interface IRuntimeModifier {

    NBTTagCompound saveToNBT();

    void readFromNBT(NBTTagCompound tag);

}
