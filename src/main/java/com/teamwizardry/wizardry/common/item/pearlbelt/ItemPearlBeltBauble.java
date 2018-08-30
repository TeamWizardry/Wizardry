package com.teamwizardry.wizardry.common.item.pearlbelt;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.api.item.wheels.IPearlWheelHolder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemPearlBeltBauble extends ItemModBauble implements IPearlWheelHolder {

	public ItemPearlBeltBauble() {
		super("pearl_belt");
		setMaxStackSize(1);
	}

	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(@NotNull ItemStack stack) {
		return BaubleType.BELT;
	}

	@Override
	public IItemHandler pearls(ItemStack stack) {
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}
}
