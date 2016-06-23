package me.lordsaad.wizardry.spells.modules.booleans;

import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleOr extends Module
{
	public ModuleOr(Module... modules)
	{
		this.modules = modules;
	}
	
	@Override
	public ModuleType getType()
	{
		return ModuleType.BOOLEAN;
	}

	@Override
	public NBTTagCompound getModuleData()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString("Type", "OR");
		return compound;
	}
}