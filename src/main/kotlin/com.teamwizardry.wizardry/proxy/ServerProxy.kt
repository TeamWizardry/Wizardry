package com.teamwizardry.wizardry.proxy

import com.teamwizardry.wizardry.client.particle.GlitterBox
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand

class ServerProxy : IProxy {
    override fun clientSetup() {}
    override fun registerHandlers() {
        //NOOP
    }

    override fun setItemStackHandHandler(hand: Hand?, stack: ItemStack?) {
        //NOOP
    }

    override fun spawnParticle(box: GlitterBox) {}

    //    @Override
    //    public void spawnKeyedParticle(KeyFramedGlitterBox box) {
    //
    //    }
    override fun openWorktableGui() {}
}