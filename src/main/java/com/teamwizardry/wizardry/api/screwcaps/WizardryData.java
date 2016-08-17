package com.teamwizardry.wizardry.api.screwcaps;

import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.bloods.BloodRegistry;
import com.teamwizardry.wizardry.api.capability.bloods.IBloodType;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 8/17/2016.
 */
public class WizardryData {

	private int mana, manaMax, burnout, burnoutMax;
	private IBloodType bloodType;

	public WizardryData() {
		mana = 0;
		manaMax = 100;
		burnout = 100;
		burnoutMax = 100;
		bloodType = BloodRegistry.HUMANBLOOD;
	}

	public WizardryData(int mana, int manaMax, int burnout, int burnoutMax, IBloodType bloodType) {
		this.mana = mana;
		this.manaMax = manaMax;
		this.burnout = burnout;
		this.burnoutMax = burnoutMax;
		this.bloodType = bloodType;
	}

	public WizardryData(NBTTagCompound compound) {
		if (compound.hasKey(Constants.Data.MANA)) this.mana = compound.getInteger(Constants.Data.MANA);
		if (compound.hasKey(Constants.Data.MAX_MANA)) this.manaMax = compound.getInteger(Constants.Data.MAX_MANA);
		if (compound.hasKey(Constants.Data.BURNOUT)) this.burnout = compound.getInteger(Constants.Data.BURNOUT);
		if (compound.hasKey(Constants.Data.MAX_BURNOUT)) this.burnoutMax = compound.getInteger(Constants.Data.MAX_BURNOUT);
		if (compound.hasKey(Constants.Data.BLOOD_TYPE)) this.bloodType = BloodRegistry.getBloodTypeByName(compound.getString(Constants.Data.BLOOD_TYPE));
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getManaMax() {
		return manaMax;
	}

	public void setManaMax(int manaMax) {
		this.manaMax = manaMax;
	}

	public int getBurnout() {
		return burnout;
	}

	public void setBurnout(int burnout) {
		this.burnout = burnout;
	}

	public int getBurnoutMax() {
		return burnoutMax;
	}

	public void setBurnoutMax(int burnoutMax) {
		this.burnoutMax = burnoutMax;
	}

	public IBloodType getBloodType() {
		return bloodType;
	}

	public void setBloodType(IBloodType bloodType) {
		this.bloodType = bloodType;
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.getTag("WIZARDRY_DATA");
		compound.setInteger(Constants.Data.MANA, mana);
		compound.setInteger(Constants.Data.MAX_MANA, manaMax);
		compound.setInteger(Constants.Data.BURNOUT, burnout);
		compound.setInteger(Constants.Data.MAX_BURNOUT, burnoutMax);
		compound.setString(Constants.Data.BLOOD_TYPE, BloodRegistry.getBloodNameByType(bloodType));
		return compound;
	}
}
