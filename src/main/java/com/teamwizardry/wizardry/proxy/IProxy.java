package com.teamwizardry.wizardry.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface IProxy {

	void registerHandlers();

	void setItemStackHandHandler(Hand hand, ItemStack stack);

	void spawnParticle(Entity entity);
}
