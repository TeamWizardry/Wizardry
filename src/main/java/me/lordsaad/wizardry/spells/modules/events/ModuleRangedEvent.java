package me.lordsaad.wizardry.spells.modules.events;

import net.minecraft.nbt.NBTTagCompound;
import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;

public class ModuleRangedEvent extends Module
{
	private Module[] modules;
	
	public ModuleRangedEvent(Module... modules)
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