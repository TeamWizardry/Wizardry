package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.wizardry.client.particle.Glitter;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ClientProxy implements IProxy {

	private Glitter glitter;

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
	public void spawnParticle(GlitterBox box) {
		if (glitter == null) {
			glitter = new Glitter();
		}
		glitter.spawn(box);
	}
}
