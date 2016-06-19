package me.lordsaad.wizardry.fluid;

import me.lordsaad.wizardry.ModItems;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.items.ItemPearl;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class FluidBlockMana extends BlockFluidClassic{

	public static final FluidBlockMana instance = new FluidBlockMana(); 
	public FluidBlockMana() 
	{
		super(FluidMana.instance, Material.WATER);
		
		this.setQuantaPerBlock(6);
		this.setUnlocalizedName("mana");
		this.setCreativeTab(Wizardry.tab);
	}

	@Override
	public Fluid getFluid()
	{
		return FluidMana.instance;
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		if(!worldIn.isRemote)
		{
			if(entityIn instanceof EntityItem)
			{
				EntityItem ei = (EntityItem) entityIn;
				ItemStack stack = ei.getEntityItem();
				ei.setDead();
				
				if(stack.getItem() == ModItems.pearl)
				{
					ItemPearl pearl = (ItemPearl)stack.getItem();
					pearl.explode(worldIn, entityIn);
					worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
				}
			}
		}
	}

	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
		return EnumBlockRenderType.MODEL;
    }
}
