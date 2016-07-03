package com.teamwizardry.wizardry.common.spell.module.booleans;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;

public class ModuleNot extends Module
{
	@Override
	public ModuleType getType()
	{
		return ModuleType.BOOLEAN;
	}
	
	@Override
	public String getDescription()
	{
		return "Will pass condition if it is false.";
	}

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}
}
