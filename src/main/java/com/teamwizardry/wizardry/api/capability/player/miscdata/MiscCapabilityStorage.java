package com.teamwizardry.wizardry.api.capability.player.miscdata;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.common.module.effects.ModuleEffectBackup;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * Created by Demoniaque on 8/16/2016.
 */
public class MiscCapabilityStorage implements IStorage<IMiscCapability> {

	public static final MiscCapabilityStorage INSTANCE = new MiscCapabilityStorage();

	@Override
	public NBTBase writeNBT(Capability<IMiscCapability> capability, IMiscCapability instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();

		if (instance.getSelectedFairyUUID() != null)
			NBTHelper.setUniqueId(nbt, "fairy_selected", instance.getSelectedFairyUUID());

		return nbt;
	}

	@Override
	public void readNBT(Capability<IMiscCapability> capability, IMiscCapability instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound tag = (NBTTagCompound) nbt;

		if (tag.hasUniqueId("fairy_selected")) {
			instance.setSelectedFairy(NBTHelper.getUniqueId(tag, "fairy_selected"));
		} else instance.setSelectedFairy(null);
	}
}
