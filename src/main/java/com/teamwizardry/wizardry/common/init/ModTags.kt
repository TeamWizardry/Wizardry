package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;

public class ModTags
{
    public static final Tag<Fluid> MANA = TagFactory.FLUID.create(Wizardry.getId("mana"));
    
    public static void init()
    {
        // no-op
    }
}
