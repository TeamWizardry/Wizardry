package com.teamwizardry.wizardry.common.init

import com.teamwizardry.wizardry.getID
import net.fabricmc.fabric.api.tag.TagFactory
import net.minecraft.fluid.Fluid
import net.minecraft.tag.Tag

object ModTags {
    val MANA: Tag<Fluid> = TagFactory.FLUID.create(getID("mana"))
    fun init() {
        // no-op
    }
}