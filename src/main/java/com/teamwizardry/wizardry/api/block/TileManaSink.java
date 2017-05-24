package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.base.block.TileMod;
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.capability.DefaultWizardryCapability;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
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
	public IWizardryCapability cap = new DefaultWizardryCapability();

	@Override
	public void update() {
		if (faucetPos != null) {
			WizardManager sink = new WizardManager(cap);
			WizardManager faucet = new WizardManager(world, faucetPos, null);

			if (!sink.isManaFull()) {
				double idealAmount = sink.getMaxMana() / 1000.0;
				double effectiveAmount = Math.max(0, faucet.getMana() - idealAmount);
				faucet.removeMana(effectiveAmount);
				sink.addMana(effectiveAmount);
			}

			faucet.removeBurnout(faucet.getMaxBurnout() / 1000.0);
		}
	}
}
