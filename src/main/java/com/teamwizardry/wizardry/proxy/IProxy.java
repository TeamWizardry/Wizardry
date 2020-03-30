package com.teamwizardry.wizardry.proxy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface IProxy {

	void registerHandlers();

	void setItemStackHandHandler(Hand hand, ItemStack stack);
}
