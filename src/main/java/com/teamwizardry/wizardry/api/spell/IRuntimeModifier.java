package com.teamwizardry.wizardry.api.spell;

import net.minecraft.nbt.NBTTagCompound;

public interface IRuntimeModifier {

    NBTTagCompound saveToNBT();

    void readFromNBT(NBTTagCompound tag);

}
