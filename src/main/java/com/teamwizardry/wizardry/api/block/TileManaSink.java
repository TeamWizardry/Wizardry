package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.base.block.TileMod;
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardManager;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.util.EnumFacing.*;

/**
 * Created by LordSaad.
 */
public class TileManaSink extends TileMod implements ITickable {

	@Save
	public BlockPos faucetPos;

	@Save
	@CapabilityProvide(sides = {DOWN, UP, NORTH, SOUTH, WEST, EAST})
	public CustomWizardryCapability cap;

	public TileManaSink(double maxMana, double maxBurnout) {
		cap = new CustomWizardryCapability(maxMana, maxBurnout);
	}

	@Override
	public void update() {
		if (faucetPos != null) {
			WizardManager sink = new WizardManager(cap);

			TileManaFaucet tileFaucet = (TileManaFaucet) world.getTileEntity(faucetPos);
			if (tileFaucet == null) return;

			WizardManager faucet = new WizardManager(tileFaucet.cap);

			if (sink.isManaFull()) return;

			double idealAmount = sink.getMaxMana() / 1000.0;
			double effectiveAmount = Math.max(0, faucet.getMana() - idealAmount);
			faucet.removeMana(effectiveAmount);
			sink.addMana(effectiveAmount);

		}
	}

	protected final boolean consumeMana(double amount) {
		WizardManager manager = new WizardManager(cap);

		if (amount <= manager.getMana()) {
			manager.removeMana(amount);
			return true;
		}
		return false;
	}
}
