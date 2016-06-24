package me.lordsaad.wizardry.spells.modules.modifiers;

import net.minecraft.nbt.NBTTagCompound;

public interface IRuntimeModifier {

	public NBTTagCompound saveToNBT();
	public void readFromNBT(NBTTagCompound tag);
	
}
