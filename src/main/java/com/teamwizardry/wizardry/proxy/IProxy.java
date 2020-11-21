package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.wizardry.client.particle.GlitterBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface IProxy {


    void registerHandlers();

    void setItemStackHandHandler(Hand hand, ItemStack stack);

    void spawnParticle(GlitterBox box);

    void openWorktableGui();
}
