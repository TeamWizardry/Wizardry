package com.teamwizardry.wizardry.api.capability;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 8/16/2016.
 */
public interface IWizardryCapability {

	double getMana();

	void setMana(double mana);

	default void setMana(double mana, Entity player) {
		setMana(mana);
		dataChanged(player);
	}

	double getMaxMana();

	void setMaxMana(double maxMana);

	default void setMaxMana(double maxMana, Entity player) {
		setMaxMana(maxMana);
		dataChanged(player);
	}

	double getBurnout();

	void setBurnout(double burnout);

	default void setBurnout(double burnout, Entity player) {
		setBurnout(burnout);
		dataChanged(player);
	}

	double getMaxBurnout();

	void setMaxBurnout(double maxBurnout);

	default void setMaxBurnout(double maxBurnout, Entity player) {
		setMaxBurnout(maxBurnout);
		dataChanged(player);
	}

	EnumBloodType getBloodType();

	void setBloodType(EnumBloodType bloodType);

	default void setBloodType(EnumBloodType bloodType, Entity player) {
		setBloodType(bloodType);
		dataChanged(player);
	}

	NBTTagCompound saveNBTData();

	void loadNBTData(NBTTagCompound compound);

	void dataChanged(Entity player);
}
