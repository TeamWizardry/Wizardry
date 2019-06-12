package com.teamwizardry.wizardry.api.capability.player.mana;

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
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 8/16/2016.
 */
public class ManaCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {

	@CapabilityInject(IManaCapability.class)
	public static final Capability<IManaCapability> manaCapability = null;
	private final IManaCapability capability;

	public ManaCapabilityProvider() {
		capability = new DefaultManaCapability();
	}

	public ManaCapabilityProvider(IManaCapability capability) {
		this.capability = capability;
	}

	@Nullable
	public static IManaCapability getCap(Entity entity) {
		return entity.getCapability(manaCapability, null);
	}

	@Nullable
	public static IManaCapability getCap(World world, BlockPos pos, EnumFacing facing) {
		if (!world.isBlockLoaded(pos)) return null;
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) return null;
		return tile.getCapability(manaCapability, facing);
	}

	@Nullable
	public static IManaCapability getCap(ItemStack stack) {
		if (BaublesSupport.isBauble(stack)) return new BaubleManaCapability(stack);
		return stack.getCapability(manaCapability, null);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == manaCapability;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if ((manaCapability != null) && (capability == manaCapability)) return (T) this.capability;
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
