package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;

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
	
	default boolean canBeInfused(ItemStack stack) {
		if( getType(stack) == EnumPearlType.MUNDANE )
			return true;
		return false;
	}
	
}
