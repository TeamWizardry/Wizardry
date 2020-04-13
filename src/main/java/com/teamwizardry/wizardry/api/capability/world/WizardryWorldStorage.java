package com.teamwizardry.wizardry.api.capability.world;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

/**
 * Project: Wizardry
 * Created by Carbon
 * Copyright (c) Carbon 2020
 */
public class WizardryWorldStorage implements Capability.IStorage<WizardryWorld> {
	public static final WizardryWorldStorage INSTANCE = new WizardryWorldStorage();
	public static final String BACKUP_NBT_TAG = "zombieBackup";

	@Nullable
	@Override
	public NBTBase writeNBT(Capability<WizardryWorld> capability, WizardryWorld instance, EnumFacing side) {
		NBTTagCompound playerMap = new NBTTagCompound();

		for(UUID playerId : instance.getBackupMap().keySet()) {
			playerMap.setInteger(playerId.toString(), instance.getBackupMap().get(playerId));
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag(BACKUP_NBT_TAG, playerMap);

		return nbt;
	}

	@Override
	public void readNBT(Capability<WizardryWorld> capability, WizardryWorld instance, EnumFacing side, NBTBase nbt) {
		HashMap<UUID, Integer> map = new HashMap<>();
		NBTTagCompound tag = (NBTTagCompound) nbt;
		NBTTagCompound playerBackup = tag.getCompoundTag(BACKUP_NBT_TAG);

		for(String playerId : playerBackup.getKeySet()) {
			map.put(UUID.fromString(playerId), playerBackup.getInteger(playerId));
		}

		instance.setBackupMap(map);
	}
}
