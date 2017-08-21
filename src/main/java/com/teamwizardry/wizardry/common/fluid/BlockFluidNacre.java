package com.teamwizardry.wizardry.common.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

public class BlockFluidNacre extends BlockFluidClassic {

	public static final BlockFluidNacre instance = new BlockFluidNacre();

	public BlockFluidNacre() {
		super(FluidNacre.instance, Material.WATER);
		setRegistryName("wizardry_nacre");
		setQuantaPerBlock(1);
		setUnlocalizedName("nacre");
	}

	@Override
	public Fluid getFluid() {
		return FluidNacre.instance;
	}

	@Nonnull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
