package me.lordsaad.wizardry.fluid;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FluidBlockMana extends BlockFluidClassic{

	public static final FluidBlockMana instance = new FluidBlockMana(); 
	public FluidBlockMana() 
	{
		super(FluidMana.instance, Material.WATER);
		
		this.setQuantaPerBlock(4);
		this.setUnlocalizedName("mana");
		this.setCreativeTab(Wizardry.tab);
	}
	
	 @Override
     public EnumBlockRenderType getRenderType(IBlockState state)
     {
     	return EnumBlockRenderType.MODEL;
     }

}
