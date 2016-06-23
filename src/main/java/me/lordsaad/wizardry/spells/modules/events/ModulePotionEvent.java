package me.lordsaad.wizardry.spells.modules.events;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModulePotionEvent extends Module
{
	public ModulePotionEvent()
	{
		
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.EFFECT;
	}

	@Override
	public NBTTagCompound getModuleData()
	{
		return null;
	}
}