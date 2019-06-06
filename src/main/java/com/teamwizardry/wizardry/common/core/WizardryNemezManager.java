package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.api.capability.world.WizardryWorld;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class WizardryNemezManager {

	public static NemezTracker getOrCreateNemezDrive(World world, BlockPos pos) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);
		HashMap<BlockPos, NemezTracker> nemezDrives = worldCap.getBlockNemezDrives();

		if (nemezDrives.containsKey(pos)) return nemezDrives.get(pos);

		return worldCap.addNemezDrive(pos, new NemezTracker());
	}

	public static NemezTracker getOrCreateNemezDrive(World world, Entity entity) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);
		HashMap<UUID, NemezTracker> nemezDrives = worldCap.getEntityNemezDrives();

		if (nemezDrives.containsKey(entity.getUniqueID())) return nemezDrives.get(entity.getUniqueID());

		return worldCap.addNemezDrive(entity.getUniqueID(), new NemezTracker());
	}

	public static NemezTracker getOrCreateNemezDrive(World world, UUID uuid) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);
		HashMap<UUID, NemezTracker> nemezDrives = worldCap.getEntityNemezDrives();

		if (nemezDrives.containsKey(uuid)) return nemezDrives.get(uuid);

		return worldCap.addNemezDrive(uuid, new NemezTracker());
	}

	@Nullable
	public static NemezTracker getAndRemoveNemezDrive(World world, BlockPos pos) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);

		if (worldCap.getBlockNemezDrives().containsKey(pos)) {
			NemezTracker tracker = worldCap.getBlockNemezDrives().get(pos);
			worldCap.removeNemezDrive(pos);
			return tracker;
		}
		return null;
	}

	@Nullable
	public static NemezTracker getAndRemoveNemezDrive(World world, Entity entity) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);

		if (worldCap.getEntityNemezDrives().containsKey(entity.getUniqueID())) {
			NemezTracker tracker = worldCap.getEntityNemezDrives().get(entity.getUniqueID());
			worldCap.removeNemezDrive(entity.getUniqueID());
			return tracker;
		}
		return null;
	}

	@Nullable
	public static NemezTracker getAndRemoveNemezDrive(World world, UUID uuid) {
		WizardryWorld worldCap = WizardryWorldCapability.get(world);

		if (worldCap.getEntityNemezDrives().containsKey(uuid)) {
			NemezTracker tracker = worldCap.getEntityNemezDrives().get(uuid);
			worldCap.removeNemezDrive(uuid);
			return tracker;
		}
		return null;
	}
}
