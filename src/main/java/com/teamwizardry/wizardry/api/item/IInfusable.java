package com.teamwizardry.wizardry.api.item;

import net.minecraft.item.ItemStack;

/**
 * Created by Saad on 6/30/2016.
 */
public interface IInfusable {

	default EnumPearlType getType(ItemStack stack) {
		if (stack.hasTagCompound())
			return stack.getTagCompound().hasKey("type") ? EnumPearlType.valueOf(stack.getTagCompound().getString("type").toUpperCase()) : EnumPearlType.MUNDANE;
		else return EnumPearlType.MUNDANE;
	}
}
