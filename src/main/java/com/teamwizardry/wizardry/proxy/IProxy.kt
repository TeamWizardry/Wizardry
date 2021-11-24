package com.teamwizardry.wizardry.proxy

import com.teamwizardry.wizardry.client.particle.GlitterBox
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand

interface IProxy {
    fun clientSetup()
    fun registerHandlers()
    fun setItemStackHandHandler(hand: Hand?, stack: ItemStack?)
    fun spawnParticle(box: GlitterBox)

    //    void spawnKeyedParticle(KeyFramedGlitterBox box);
    fun openWorktableGui()
}