package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidMana extends Fluid {
    public static final FluidMana instance = new FluidMana();

    public FluidMana() {
        super("wizardry.mana_fluid", new ResourceLocation(Wizardry.MODID, "fluid/mana_still"), new ResourceLocation(Wizardry.MODID, "fluid/mana_flowing"));
        FluidRegistry.registerFluid(this);
        setViscosity(500);
        setTemperature(200);
        setLuminosity(3);
        setDensity(500);
    }
}
