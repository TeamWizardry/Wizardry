package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.common.spell.effect.EffectBurn;
import com.teamwizardry.wizardry.common.spell.shape.ShapeRay;

import net.minecraftforge.registries.IForgeRegistry;

public class PatternInit
{
    public static void init(IForgeRegistry<Pattern> registry)
    {
        // Shapes
        registry.registerAll(new ShapeRay().setRegistryName(Wizardry.MODID, "ray"),
                             new EffectBurn().setRegistryName(Wizardry.MODID, "burn"));
    }
}
