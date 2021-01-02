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
	private long mana;

	@Save
	private long maxMana;

	@Save
	private long burnout;

	@Save
	private long maxBurnout;

	public ManaCapability(long mana, long maxMana, long burnout, long maxBurnout) {
		this.mana = mana;
		this.maxMana = maxMana;
		this.burnout = burnout;
		this.maxBurnout = maxBurnout;
	}

	@Override
	public long getMana() {
		return mana;
	}

	@Override
	public void setMana(long mana) {
		this.mana = mana;
	}

	@Override
	public long getMaxMana() {
		return maxMana;
	}

	@Override
	public void setMaxMana(long maxMana) {
		this.maxMana = maxMana;
	}

	@Override
	public long getBurnout() {
		return burnout;
	}

	@Override
	public void setBurnout(long burnout) {
		this.burnout = burnout;
	}

	@Override
	public long getMaxBurnout() {
		return maxBurnout;
	}

	@Override
	public void setMaxBurnout(long maxBurnout) {
		this.maxBurnout = maxBurnout;
	}
}
