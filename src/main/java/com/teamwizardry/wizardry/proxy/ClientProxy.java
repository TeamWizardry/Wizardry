package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.wizardry.client.gui.WorktableGUI;
import com.teamwizardry.wizardry.client.particle.Glitter;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ClientProxy implements IProxy {

    private final Glitter glitter = new Glitter();

    @Override
    public void registerHandlers() {
        glitter.addToGame();
    }

    @Override
    public void setItemStackHandHandler(Hand hand, ItemStack stack) {
		/*if (hand == Hand.MAIN_HAND)
			itemStackMainHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);
		else itemStackOffHandHandler.invoke(Minecraft.getInstance().getItemRenderer(), stack);*/
    }

    @Override
    public void spawnParticle(GlitterBox box) {
        glitter.spawn(box);
    }

    @Override
    public void openWorktableGui() {
        Minecraft.getInstance().displayGuiScreen(new WorktableGUI());
    }
}
