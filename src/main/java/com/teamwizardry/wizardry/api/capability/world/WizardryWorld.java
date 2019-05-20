package com.teamwizardry.wizardry.api.capability.world;

import com.teamwizardry.wizardry.api.SpellObjectManager;
import com.teamwizardry.wizardry.common.core.nemez.NemezTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.HashMap;

public interface WizardryWorld extends ICapabilitySerializable<NBTTagCompound> {

	SpellObjectManager getSpellObjectManager();

	NemezTracker addNemezDrive(BlockPos pos, NemezTracker nemezDrive);

	void removeNemezDrive(BlockPos pos);

	HashMap<BlockPos, NemezTracker> getNemezDrives();

}
