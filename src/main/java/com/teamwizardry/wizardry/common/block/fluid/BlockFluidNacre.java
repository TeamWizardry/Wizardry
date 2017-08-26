package com.teamwizardry.wizardry.common.block.fluid;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidNacre extends BlockFluidClassic {

	public BlockFluidNacre() {
		super(FluidNacre.instance, Material.WATER);
		setRegistryName("nacre");
		setQuantaPerBlock(1);
		setUnlocalizedName("nacre");
	}

	@Override
	public Fluid getFluid() {
		return FluidNacre.instance;
	}
}
