package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FluidBlockNacre extends BlockFluidClassic {

	public static final FluidBlockNacre instance = new FluidBlockNacre();

	public FluidBlockNacre() {
		super(FluidNacre.instance, ModBlocks.NACRE_MATERIAL);
		GameRegistry.register(this, new ResourceLocation(Wizardry.MODID, "nacre"));
		setQuantaPerBlock(1);
		setUnlocalizedName("nacre");
	}

	@Override
	public Fluid getFluid() {
		return FluidNacre.instance;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
