package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.common.spell.shape.ShapeTouch;

import net.minecraftforge.registries.IForgeRegistry;

public class PatternInit
{
    public static void init(IForgeRegistry<Pattern> registry)
    {
        // Shapes
        registry.registerAll(new ShapeTouch().setRegistryName(Wizardry.MODID, "beam"));
    }
}
