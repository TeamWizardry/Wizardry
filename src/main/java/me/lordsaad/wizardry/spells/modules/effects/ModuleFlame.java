package me.lordsaad.wizardry.spells.modules.effects;

import me.lordsaad.wizardry.api.modules.IModule;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleFlame implements IModule
{
	private IModule[] modules;
	
	public ModuleFlame(IModule... modules)
	{
		this.modules = modules;
	}
	
    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }
    
	@Override
	public NBTTagCompound getModuleData()
	{
		return null;
	}
}