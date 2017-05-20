package com.teamwizardry.wizardry.api.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 8/16/2016.
 */
public interface IWizardryCapability {

	double getMana();

	void setMana(double mana, EntityPlayer player);

	double getMaxMana();

	void setMaxMana(double maxMana, EntityPlayer player);

	double getBurnout();

	void setBurnout(double burnout, EntityPlayer player);

	double getMaxBurnout();

	void setMaxBurnout(double maxBurnout, EntityPlayer player);

	EnumBloodType getBloodType();

	void setBloodType(EnumBloodType bloodType, EntityPlayer player);

	NBTTagCompound saveNBTData();

	void loadNBTData(NBTTagCompound compound);

	void dataChanged(EntityPlayer player);
}
