package me.lordsaad.wizardry.spells.modules.events;

import net.minecraft.nbt.NBTTagCompound;
import me.lordsaad.wizardry.api.modules.IModule;
import me.lordsaad.wizardry.spells.modules.ModuleType;

public class ModuleRangedEvent implements IModule
{
	private IModule[] modules;
	
	public ModuleRangedEvent(IModule... modules)
	{
		this.modules = modules;
	}
	
    @Override
    public ModuleType getType() {
        return ModuleType.EVENT;
    }
    
	@Override
	public NBTTagCompound getModuleData()
	{
		return null;
	}
}