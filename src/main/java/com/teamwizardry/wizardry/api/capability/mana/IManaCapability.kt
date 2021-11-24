package com.teamwizardry.wizardry.api.capability.mana;

public interface IManaCapability {
	double getMana();

	void setMana(double mana);

	double getMaxMana();

	void setMaxMana(double maxMana);

	double getBurnout();

	void setBurnout(double burnout);

	double getMaxBurnout();

	void setMaxBurnout(double maxBurnout);
}
