package me.lordsaad.wizardry.spells.modules.events;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleBlinkEvent extends Module
{
	public ModuleBlinkEvent()
	{
		
	}

	@Override
	public ModuleType getType()
	{
		return ModuleType.EVENT;
	}

	@Override
	public NBTTagCompound getModuleData()
	{
		return null;
	}
}