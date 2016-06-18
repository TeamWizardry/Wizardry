package me.lordsaad.wizardry.fluid;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
		return EnumBlockRenderType.MODEL;
    }
}
