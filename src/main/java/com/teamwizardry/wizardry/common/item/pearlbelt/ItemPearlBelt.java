package com.teamwizardry.wizardry.common.item.pearlbelt;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.api.item.wheels.IPearlWheelHolder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemPearlBelt extends ItemMod implements IPearlWheelHolder {

	public ItemPearlBelt() {
		super("pearl_belt");
		setMaxStackSize(1);
	}

	@Override
	public IItemHandler pearls(ItemStack stack) {
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}
}
