package com.teamwizardry.wizardry.api.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

/**
 * Created by Saad on 8/16/2016.
 */
public class WizardryCapabilityProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

	@CapabilityInject(IWizardryCapability.class)
	public static final Capability<IWizardryCapability> wizardryCapability = null;
	private final IWizardryCapability capability;

	public WizardryCapabilityProvider() {
		capability = new DefaultWizardryCapability();
	}

	public WizardryCapabilityProvider(IWizardryCapability capability) {
		this.capability = capability;
	}

	public static IWizardryCapability get(EntityPlayer player) {
		return ((player != null) && player.hasCapability(wizardryCapability, null)) ? player.getCapability(wizardryCapability, null) : null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == wizardryCapability;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if ((wizardryCapability != null) && (capability == wizardryCapability)) return (T) this.capability;
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return capability.saveNBTData();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		capability.loadNBTData(nbt);
	}

}
