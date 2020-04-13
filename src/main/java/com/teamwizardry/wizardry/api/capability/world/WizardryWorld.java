package com.teamwizardry.wizardry.api.capability.world;

import com.teamwizardry.wizardry.api.SpellObjectManager;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.HashMap;
import java.util.UUID;

public interface WizardryWorld extends ICapabilitySerializable<NBTTagCompound> {

	SpellObjectManager getSpellObjectManager();

	NemezTracker addNemezDrive(BlockPos pos, NemezTracker nemezDrive);

	NemezTracker addNemezDrive(UUID uuid, NemezTracker nemezDrive);

	void removeNemezDrive(BlockPos pos);

	void removeNemezDrive(UUID uuid);

	HashMap<BlockPos, NemezTracker> getBlockNemezDrives();

	HashMap<UUID, NemezTracker> getEntityNemezDrives();

	int getBackupCount(UUID player);

	void setBackupCount(UUID player, int count);

	void incBackupCount(UUID player);

	void decBackupCount(UUID player);

	HashMap<UUID, Integer> getBackupMap();

	void setBackupMap(HashMap<UUID, Integer> map);
}
