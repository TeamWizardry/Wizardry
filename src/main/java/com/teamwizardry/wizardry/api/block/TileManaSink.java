package com.teamwizardry.wizardry.api.block;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.WEST;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;

import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

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
			CapManager sink = new CapManager(cap);

			TileManaFaucet tileFaucet = (TileManaFaucet) world.getTileEntity(faucetPos);
			if (tileFaucet == null) return;

			CapManager faucet = new CapManager(tileFaucet.cap);

			if (sink.isManaFull()) return;

			double idealAmount = 100;
			if (faucet.getMana() < idealAmount) return;
			faucet.removeMana(idealAmount);
			sink.addMana(idealAmount);

			ClientRunnable.run(() -> {
				Wizardry.proxy.tileManaSinkParticles(world, faucetPos, faucetPos);
			});

		}
	}

	protected final boolean consumeMana(double amount) {
		CapManager manager = new CapManager(cap);

		if (amount <= manager.getMana()) {
			manager.removeMana(amount);
			return true;
		}
		return false;
	}
}
