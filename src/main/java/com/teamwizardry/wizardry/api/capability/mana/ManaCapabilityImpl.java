package com.teamwizardry.wizardry.api.capability.mana;

public class ManaCapabilityImpl implements IManaCapability {
	private long mana, maxMana;
	private long burnout, maxBurnout;

	public ManaCapabilityImpl(long mana, long maxMana, long burnout, long maxBurnout) {
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
