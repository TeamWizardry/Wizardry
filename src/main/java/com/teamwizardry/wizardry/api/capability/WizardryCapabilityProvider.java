package com.teamwizardry.wizardry.api.capability;

import com.teamwizardry.wizardry.api.item.BaublesSupport;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 8/16/2016.
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

	@Nullable
	public static IWizardryCapability getCap(Entity entity) {
		return entity.getCapability(wizardryCapability, null);
	}

	@Nullable
	public static IWizardryCapability getCap(World world, BlockPos pos, EnumFacing facing) {
		if (!world.isBlockLoaded(pos)) return null;
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) return null;
		return tile.getCapability(wizardryCapability, facing);
	}

	@Nullable
	public static IWizardryCapability getCap(ItemStack stack) {
		if (BaublesSupport.isBauble(stack)) return new BaubleWizardryCapability(stack);
		return stack.getCapability(wizardryCapability, null);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == wizardryCapability;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if ((wizardryCapability != null) && (capability == wizardryCapability)) return (T) this.capability;
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
