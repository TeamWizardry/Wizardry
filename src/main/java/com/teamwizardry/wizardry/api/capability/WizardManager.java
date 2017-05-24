package com.teamwizardry.wizardry.api.capability;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by LordSaad.
 */
public final class WizardManager {

	@Nullable
	private IWizardryCapability cap;

	public WizardManager(@Nullable Entity entity) {
		cap = WizardryCapabilityProvider.getCap(entity);
	}

	public WizardManager(@Nullable IWizardryCapability cap) {
		this.cap = cap;
	}

	public WizardManager(World world, BlockPos pos, @Nullable EnumFacing facing) {
		cap = WizardryCapabilityProvider.getCap(world, pos, facing);
	}

	public void addMana(double mana) {
		setMana(getMana() + mana);
	}

	public void removeMana(double mana) {
		setMana(getMana() - mana);
	}

	public void addBurnout(double burnout) {
		setBurnout(getBurnout() + burnout);
	}

	public void removeBurnout(double burnout) {
		setBurnout(getBurnout() - burnout);
	}

	public double getMaxMana() {
		if (cap == null) return 0;
		return cap.getMaxMana();
	}

	public void setMaxMana(double mana) {
		if (cap == null) return;
		cap.setMaxMana(mana);
		if (getMana() > mana) setMana(mana);
	}

	public double getMana() {
		if (cap == null) return 0;
		return cap.getMana();
	}

	public void setMana(double mana) {
		if (cap == null) return;
		cap.setMana(Math.min(Math.max(0, mana), getMaxMana()));
	}

	public double getBurnout() {
		if (cap == null) return 0;
		return cap.getBurnout();
	}

	public void setBurnout(double burnout) {

		if (cap == null) return;
		cap.setBurnout(Math.max(0, Math.min(burnout, getMaxBurnout())));
	}

	public double getMaxBurnout() {
		if (cap == null) return 0;
		return cap.getMaxBurnout();
	}

	public void setMaxBurnout(double burnout) {
		if (cap == null) return;
		cap.setMaxBurnout(burnout);
		if (getBurnout() > burnout) setBurnout(burnout);
	}

	public boolean isManaFull() {
		return cap != null && cap.getMana() >= cap.getMaxMana();
	}

	public boolean isBurnoutFull() {
		return cap != null && cap.getBurnout() >= cap.getMaxBurnout();
	}

	public boolean isManaEmpty() {
		return cap != null && cap.getMana() <= 0;
	}

	public boolean isBurnoutEmpty() {
		return cap != null && cap.getBurnout() <= 0;
	}
}
