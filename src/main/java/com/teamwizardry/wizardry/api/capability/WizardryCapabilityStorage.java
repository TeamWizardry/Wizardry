package com.teamwizardry.wizardry.api.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

import static com.teamwizardry.wizardry.api.Constants.Data.*;

/**
 * Created by Demoniaque on 8/16/2016.
 */
public class WizardryCapabilityStorage implements IStorage<IWizardryCapability> {

	public static final WizardryCapabilityStorage INSTANCE = new WizardryCapabilityStorage();

	@Override
	public NBTBase writeNBT(Capability<IWizardryCapability> capability, IWizardryCapability instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();
		if (instance.getBloodType() != null)
			nbt.setString(BLOOD_TYPE, instance.getBloodType().id);
		nbt.setDouble(MAX_MANA, instance.getMaxMana());
		nbt.setDouble(MAX_BURNOUT, instance.getMaxBurnout());
		nbt.setDouble(MANA, instance.getMana());
		nbt.setDouble(BURNOUT, instance.getBurnout());

		return nbt;
	}

	@Override
	public void readNBT(Capability<IWizardryCapability> capability, IWizardryCapability instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound tag = (NBTTagCompound) nbt;

		instance.setMana(tag.getDouble(MANA));
		instance.setMaxMana(tag.getDouble(MAX_MANA));
		instance.setBurnout(tag.getDouble(BURNOUT));
		instance.setMaxBurnout(tag.getDouble(MAX_BURNOUT));
		if (tag.hasKey(BLOOD_TYPE))
			instance.setBloodType(EnumBloodType.getType(tag.getString(BLOOD_TYPE)));
	}
}
