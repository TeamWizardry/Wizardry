package me.lordsaad.wizardry.spells.modules.effects;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleSaturation extends Module
{
	public ModuleSaturation()
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