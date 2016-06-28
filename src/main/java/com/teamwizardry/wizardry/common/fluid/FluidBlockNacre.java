package com.teamwizardry.wizardry.common.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FluidBlockNacre extends BlockFluidClassic {

    public static final FluidBlockNacre instance = new FluidBlockNacre();

    public FluidBlockNacre() {
        super(FluidNacre.instance, Material.WATER);
        GameRegistry.registerBlock(this, "nacre");
        this.setQuantaPerBlock(6);
        this.setUnlocalizedName("nacre");
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
