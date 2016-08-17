package com.teamwizardry.wizardry.api.capability;

import com.teamwizardry.wizardry.api.capability.bloods.BloodRegistry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import static com.teamwizardry.wizardry.api.Constants.Data.*;

/**
 * Created by Saad on 8/16/2016.
 */
public class WizardryCapabilityStorage implements Capability.IStorage<IWizardryCapability> {

	public static final WizardryCapabilityStorage INSTANCE = new WizardryCapabilityStorage();

	@Override
	public NBTBase writeNBT(Capability<IWizardryCapability> capability, IWizardryCapability instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString(BLOOD_TYPE, BloodRegistry.getBloodNameByType(instance.getBloodType()));
		nbt.setInteger(MAX_MANA, instance.getMaxMana());
		nbt.setInteger(MAX_BURNOUT, instance.getMaxBurnout());
		nbt.setInteger(MANA, instance.getMana());
		nbt.setInteger(BURNOUT, instance.getBurnout());
		return nbt;
	}

	@Override
	public void readNBT(Capability<IWizardryCapability> capability, IWizardryCapability instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound tag = (NBTTagCompound) nbt;
		((DefaultWizardryCapability) instance).mana = tag.getInteger(MANA);
		((DefaultWizardryCapability) instance).maxMana = tag.getInteger(MAX_MANA);
		((DefaultWizardryCapability) instance).burnout = tag.getInteger(BURNOUT);
		((DefaultWizardryCapability) instance).maxBurnout = tag.getInteger(MAX_BURNOUT);
		((DefaultWizardryCapability) instance).bloodType = BloodRegistry.getBloodTypeByName(tag.getString(BLOOD_TYPE));
	}
}