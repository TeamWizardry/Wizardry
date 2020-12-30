package com.teamwizardry.wizardry.api.capability.mana;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface IManaCapability extends ICapabilityProvider {

	long getMana();

	void setMana(long mana);

	long getMaxMana();

	void setMaxMana(long maxMana);

	long getBurnout();

	void setBurnout(long burnout);

	long getMaxBurnout();

	void setMaxBurnout(long maxBurnout);
}
