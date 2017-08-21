package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;

import static net.minecraft.util.EnumFacing.*;

/**
 * Created by LordSaad.
 */
public class TileManaFaucet extends TileMod {

	@Save
	@CapabilityProvide(sides = {DOWN, UP, NORTH, SOUTH, WEST, EAST})
	public CustomWizardryCapability cap;

	public TileManaFaucet(double maxMana, double maxBurnout) {
		cap = new CustomWizardryCapability(maxMana, maxBurnout);
	}

	protected final boolean addMana(double amount) {
		CapManager manager = new CapManager(cap);
		if (amount > cap.getMaxMana() - cap.getMana()) {
			manager.setMana(manager.getMaxMana());
			return false;
		}
		manager.addMana(amount);
		return true;
	}
}
