package com.teamwizardry.wizardry.proxy

import com.teamwizardry.wizardry.common.PhysicsGlitterBox
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand

interface IProxy {
    fun clientSetup()
    fun registerHandlers()
    fun setItemStackHandHandler(hand: Hand?, stack: ItemStack?)
    fun spawnPhysicsGlitter(box: PhysicsGlitterBox)

    //    void spawnKeyedParticle(KeyFramedGlitterBox box);
    fun openWorktableGui()
}