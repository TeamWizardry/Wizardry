package com.teamwizardry.wizardry.api.capability.player.miscdata;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 8/16/2016.
 */
public class MiscCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {

	@CapabilityInject(IMiscCapability.class)
	public static final Capability<IMiscCapability> miscCapability = null;
	private final IMiscCapability capability;

	public MiscCapabilityProvider() {
		capability = new DefaultMiscCapability();
	}

	public MiscCapabilityProvider(IMiscCapability capability) {
		this.capability = capability;
	}

	@Nullable
	public static IMiscCapability getCap(Entity entity) {
		return entity.getCapability(miscCapability, null);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == miscCapability;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if ((miscCapability != null) && (capability == miscCapability)) return (T) this.capability;
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return capability.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		capability.deserializeNBT(nbt);
	}

}
