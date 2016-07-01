package com.teamwizardry.wizardry.common.fluid;

import com.teamwizardry.wizardry.init.ModItems;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FluidBlockNacre extends BlockFluidClassic {

    public static final FluidBlockNacre instance = new FluidBlockNacre();

    public FluidBlockNacre() {
        super(FluidNacre.instance, Material.WATER);
        GameRegistry.registerBlock(this, "nacre");
        this.setQuantaPerBlock(1);
        this.setUnlocalizedName("nacre");
    }
    
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
    	if (!worldIn.isRemote) {
    		if (entityIn instanceof EntityItem && new BlockPos(entityIn.getPositionVector()).equals(pos) && state.getValue(BlockFluidClassic.LEVEL) == 0) {
    			
    			EntityItem ei = (EntityItem) entityIn;
                ItemStack stack = ei.getEntityItem();
                
                if(stack.getItem() == ModItems.PEARL_GLASS)
                {           
                	 if (stack.hasTagCompound()) {
                         NBTTagCompound compound = stack.getTagCompound();
                         if(compound.hasKey("quality")) {
                        	 compound.setInteger("quality", compound.getInteger("quality") + 1);
                         } else {
                        	 compound.setInteger("quality", 0);
                         }
                	 } else {
                		 stack.setTagCompound(new NBTTagCompound());
                	 }
                }
    		}
    	}
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
