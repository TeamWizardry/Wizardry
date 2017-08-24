package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidNacre extends Fluid {
	public static final FluidNacre instance = new FluidNacre();

	public FluidNacre() {
		super("nacre_fluid",
				new ResourceLocation(Wizardry.MODID, "fluid/nacre_still"),
				new ResourceLocation(Wizardry.MODID, "fluid/nacre_flowing"));
		FluidRegistry.registerFluid(this);
		FluidRegistry.addBucketForFluid(this);
		setViscosity(500);
		setDensity(500);
	}
}
