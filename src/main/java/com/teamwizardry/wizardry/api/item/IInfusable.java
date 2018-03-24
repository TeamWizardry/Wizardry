package com.teamwizardry.wizardry.api.item;

import net.minecraft.item.ItemStack;

/**
 * Implement this interface on an item to have it become spell infusable
 * Used by nacre pearls
 */
public interface IInfusable {

	default EnumPearlType getType(ItemStack stack) {
		if (stack.hasTagCompound())
			return stack.getTagCompound().hasKey("type") ? EnumPearlType.valueOf(stack.getTagCompound().getString("type").toUpperCase()) : EnumPearlType.MUNDANE;
		else return EnumPearlType.MUNDANE;
	}
}
