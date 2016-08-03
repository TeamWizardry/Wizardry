package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FluidBlockNacre extends BlockFluidClassic {

    public static final FluidBlockNacre instance = new FluidBlockNacre();

    public FluidBlockNacre() {
        super(FluidNacre.instance, ModBlocks.NACRE_MATERIAL);
        GameRegistry.registerBlock(this, "nacre");
        this.setQuantaPerBlock(1);
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
