package com.teamwizardry.wizardry.api.capability;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by Demoniaque on 8/16/2016.
 */
public interface IWizardryCapability extends INBTSerializable<NBTTagCompound> {

	double getMana();

	void setMana(double mana);

	double getMaxMana();

	void setMaxMana(double maxMana);

	double getBurnout();

	void setBurnout(double burnout);

	double getMaxBurnout();

	void setMaxBurnout(double maxBurnout);

	EnumBloodType getBloodType();

	void setBloodType(EnumBloodType bloodType);

	void dataChanged(Entity player);
}
