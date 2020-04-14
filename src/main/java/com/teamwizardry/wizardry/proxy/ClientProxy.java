package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.wizardry.client.TestParticleSystem;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ClientProxy implements IProxy {


	@Override
	public void registerHandlers() {
		//NOOP
	}

	@Override
	public void setItemStackHandHandler(Hand hand, ItemStack stack) {
		/*if (hand == Hand.MAIN_HAND)
			itemStackMainHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);
		else itemStackOffHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);*/
	}

    @Override
    public void spawnParticle(Entity entity) {
        TestParticleSystem.INSTANCE.spawn(entity);
    }
}
