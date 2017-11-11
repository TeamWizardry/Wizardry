package com.teamwizardry.wizardry.common.item;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.IHalo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Saad on 8/30/2016.
 */
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemHaloBauble extends ItemModBauble implements IHalo {

	public ItemHaloBauble() {
		super("halo");
		setMaxStackSize(1);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new WizardryCapabilityProvider(new CustomWizardryCapability(70000, 70000, 0, 0));
	}

	@Override
	public void onEquippedOrLoadedIntoWorld(@NotNull ItemStack stack, @NotNull EntityLivingBase player) {
		super.onEquippedOrLoadedIntoWorld(stack, player);
		new CapManager(stack).setEntity(player).sync();
	}

	@Override
	public void onEquipped(@NotNull ItemStack stack, @NotNull EntityLivingBase player) {
		super.onEquipped(stack, player);
		new CapManager(stack).setEntity(player).sync();
	}

	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
		new CapManager(itemstack).setEntity(player).sync();
	}

	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(@NotNull ItemStack itemStack) {
		return BaubleType.HEAD;
	}
}
