package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.wizardry.client.particle.GlitterBox;
import com.teamwizardry.wizardry.client.particle.KeyFramedGlitterBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface IProxy {

    void clientSetup();

    void registerHandlers();

    void setItemStackHandHandler(Hand hand, ItemStack stack);

    void spawnParticle(GlitterBox box);

    void spawnKeyedParticle(KeyFramedGlitterBox box);

    void openWorktableGui();
}
