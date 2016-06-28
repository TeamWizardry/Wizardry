package com.teamwizardry.wizardry.fluid;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.IExplodable;
import com.teamwizardry.wizardry.api.trackers.BookTrackerObject;
import com.teamwizardry.wizardry.particles.SparkleFX;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Random;

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
