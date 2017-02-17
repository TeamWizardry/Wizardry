package com.teamwizardry.wizardry.api.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 8/16/2016.
 */
public interface IWizardryCapability {

	int getMana();

	void setMana(int mana, EntityPlayer player);

	int getMaxMana();

	void setMaxMana(int maxMana, EntityPlayer player);

	int getBurnout();

	void setBurnout(int burnout, EntityPlayer player);

	int getMaxBurnout();

	void setMaxBurnout(int maxBurnout, EntityPlayer player);

	EnumBloodType getBloodType();

	void setBloodType(EnumBloodType bloodType, EntityPlayer player);

	NBTTagCompound saveNBTData();

	void loadNBTData(NBTTagCompound compound);

	void dataChanged(EntityPlayer player);
}
