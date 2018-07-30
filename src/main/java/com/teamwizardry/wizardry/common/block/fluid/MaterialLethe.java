package com.teamwizardry.wizardry.common.block.fluid;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;

public class MaterialLethe extends MaterialLiquid	
{
	public MaterialLethe(MapColor color)
	{
		super(color);
		setNoPushMobility();
	}
	
	@Override
	public boolean blocksMovement()
	{
		return true;
	}
}
