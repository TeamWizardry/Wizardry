package com.teamwizardry.wizardry.api.capability.mana;

import com.teamwizardry.librarianlib.foundation.capability.BaseCapability;
import com.teamwizardry.librarianlib.prism.Save;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/*
* Written by Carbon
* Mana Capability default implementation
* Thank u code <3
* */
public class ManaCapability extends BaseCapability implements IManaCapability {
	@CapabilityInject(IManaCapability.class)
	public static Capability<IManaCapability> MANA_CAPABILITY;

	@Save
	private double mana;

	@Save
	private double maxMana;

	@Save
	private double burnout;

	@Save
	private double maxBurnout;

	public ManaCapability(double mana, double maxMana, double burnout, double maxBurnout) {
		this.mana = mana;
		this.maxMana = maxMana;
		this.burnout = burnout;
		this.maxBurnout = maxBurnout;
	}

	@Override
	public double getMana() {
		return mana;
	}

	@Override
	public void setMana(double mana) {
		this.mana = mana;
	}

	@Override
	public double getMaxMana() {
		return maxMana;
	}

	@Override
	public void setMaxMana(double maxMana) {
		this.maxMana = maxMana;
	}

	@Override
	public double getBurnout() {
		return burnout;
	}

	@Override
	public void setBurnout(double burnout) {
		this.burnout = burnout;
	}

	@Override
	public double getMaxBurnout() {
		return maxBurnout;
	}

	@Override
	public void setMaxBurnout(double maxBurnout) {
		this.maxBurnout = maxBurnout;
	}
}
