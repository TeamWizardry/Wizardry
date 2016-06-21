package me.lordsaad.wizardry.spells.modules.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import me.lordsaad.wizardry.api.modules.IModule;
import me.lordsaad.wizardry.spells.modules.ModuleType;

public class ModuleSilent implements IModule
{
	private IModule[] modules;
	
	public ModuleSilent(IModule... modules)
	{
		this.modules = modules;
	}
	
    @Override
    public ModuleType getType() {
        return ModuleType.MODIFIER;
    }
    
	@Override
	public NBTTagCompound getModuleData()
	{
		return null;
	}
}