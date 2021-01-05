package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.common.spell.effect.EffectArcane;
import com.teamwizardry.wizardry.common.spell.effect.EffectBurn;
import com.teamwizardry.wizardry.common.spell.shape.ShapeRay;
import com.teamwizardry.wizardry.common.spell.shape.ShapeZone;

import net.minecraftforge.registries.IForgeRegistry;

public class PatternInit
{
    public static void init(IForgeRegistry<Pattern> registry)
    {
        // Shapes
        registry.registerAll(new ShapeRay().setRegistryName(Wizardry.MODID, "ray"),
                             new ShapeZone().setRegistryName(Wizardry.MODID, "zone")
                             );
        
        // Effects
        registry.registerAll(new EffectBurn().setRegistryName(Wizardry.MODID, "burn"),
                             new EffectArcane().setRegistryName(Wizardry.MODID, "arcane"));
    }
}
