package com.teamwizardry.wizardry.api.capability.mana;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManaCapabilityImpl implements IManaCapability {
	public static Capability<IManaCapability> manaCapability = null;

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

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return LazyOptional.empty();
	}
}
