package com.teamwizardry.wizardry.api.capability;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Demoniaque.
 */
public final class CapManager {

	@Nullable
	private IWizardryCapability cap;
	private Entity entity;
	private boolean manualSync = false, somethingChanged = false;

	public CapManager(@Nullable Entity entity) {
		this.entity = entity;
		if (entity != null) {
			cap = WizardryCapabilityProvider.getCap(entity);
		}
	}

	public CapManager(@Nullable ItemStack stack) {
		if (stack != null) {
			cap = WizardryCapabilityProvider.getCap(stack);
		}
	}

	public CapManager(@Nullable IWizardryCapability cap) {
		this.cap = cap;
	}

	public CapManager(World world, BlockPos pos, @Nullable EnumFacing facing) {
		cap = WizardryCapabilityProvider.getCap(world, pos, facing);
	}

	public void sync() {
		if (cap != null && entity != null) {
			cap.dataChanged(entity);
		}
	}

	public CapManager setEntity(Entity entity) {
		this.entity = entity;
		if (entity != null) {
			cap = WizardryCapabilityProvider.getCap(entity);
		}

		return this;
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

		boolean change = false;
		if (getMaxMana() != mana) {
			cap.setMaxMana(mana);
			change = true;
		}
		if (getMana() > mana) {
			cap.setMana(mana);
			change = true;
		}

		somethingChanged = change;

		if (change && !isManualSync()) sync();
	}

	public double getMana() {
		if (cap == null) return 0;
		return cap.getMana();
	}

	public void setMana(double mana) {
		if (cap == null) return;
		double clamped = MathHelper.clamp(mana, 0, getMaxMana());

		if (cap.getMana() != clamped) {
			cap.setMana(clamped);

			somethingChanged = true;
			if (!isManualSync()) sync();
		}
	}

	public double getBurnout() {
		if (cap == null) return 0;
		return cap.getBurnout();
	}

	public void setBurnout(double burnout) {
		if (cap == null) return;
		double clamped = MathHelper.clamp(burnout, 0, getMaxBurnout());

		if (cap.getBurnout() != clamped) {
			cap.setBurnout(clamped);

			somethingChanged = true;
			if (!isManualSync()) sync();
		}
	}

	public double getMaxBurnout() {
		if (cap == null) return 0;
		return cap.getMaxBurnout();
	}

	public void setMaxBurnout(double burnout) {
		if (cap == null) return;

		boolean change = false;
		if (getMaxBurnout() != burnout) {
			cap.setMaxBurnout(burnout);
			change = true;
		}
		if (getBurnout() > burnout) {
			cap.setBurnout(burnout);
			change = true;
		}

		somethingChanged = change;
		if (change && !isManualSync()) sync();
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

	@Nullable
	public IWizardryCapability getCap() {
		return cap;
	}

	public boolean isManualSync() {
		return manualSync;
	}

	public CapManager setManualSync(boolean manualSync) {
		this.manualSync = manualSync;
		return this;
	}

	public boolean isSomethingChanged() {
		return somethingChanged;
	}
}
