package com.teamwizardry.wizardry.api.spell;

import net.minecraft.item.ItemStack;

public interface IRequireItem
{
	public void handle(ItemStack stack);
}
