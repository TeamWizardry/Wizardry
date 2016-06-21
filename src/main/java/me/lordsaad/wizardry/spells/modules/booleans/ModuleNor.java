package me.lordsaad.wizardry.spells.modules.booleans;

import me.lordsaad.wizardry.api.modules.IModule;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ModuleNor implements IModule
{
	private IModule[] modules;
	
	public ModuleNor(IModule... modules)
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
		
		NBTTagList list = new NBTTagList();
		for (IModule module : modules)
			list.appendTag(module.getModuleData());
		compound.setTag("Modules", list);
		return compound;
	}
}