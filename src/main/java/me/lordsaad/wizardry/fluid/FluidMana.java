package me.lordsaad.wizardry.fluid;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidMana extends Fluid
{
	public static final FluidMana instance = new FluidMana();
	
	public FluidMana() 
	{
		super("mana_fluid", new ResourceLocation(Wizardry.MODID+":"+"fluid/mana_still"), new ResourceLocation(Wizardry.MODID+":"+"fluid/mana_flowing"));
	}

}
