package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;

public class SpellNemezTracker {

	public static NemezTracker getOrCreateNemezDrive(World world, BlockPos pos) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);
		HashMap<BlockPos, NemezTracker> nemezDrives = worldCap.getNemezDrives();

		if (nemezDrives.containsKey(pos)) return nemezDrives.get(pos);

		return worldCap.addNemezDrive(pos, new NemezTracker());
	}

	@Nullable
	public static NemezTracker getNemezDrive(World world, BlockPos pos) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);

		if (worldCap.getNemezDrives().containsKey(pos))
			return worldCap.getNemezDrives().get(pos);
		return null;
	}

	public static void removeNemezDrive(World world, BlockPos pos) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);

		worldCap.removeNemezDrive(pos);
	}
}
