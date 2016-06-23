package me.lordsaad.wizardry.spells.modules.effects;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleWater extends Module
{
	public ModuleWater(Module... modules)
	{
		this.modules = modules;
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