package com.teamwizardry.wizardry.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ServerProxy implements IProxy {
	@Override
	public void registerHandlers() {
		//NOOP
	}

	@Override
	public void setItemStackHandHandler(Hand hand, ItemStack stack) {
		//NOOP
	}

    @Override
    public void spawnParticle(Entity entity) {
        //NOOP
    }
}
