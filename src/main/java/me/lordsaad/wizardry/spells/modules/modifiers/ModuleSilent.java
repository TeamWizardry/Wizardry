package me.lordsaad.wizardry.spells.modules.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import me.lordsaad.wizardry.api.modules.Module;
import me.lordsaad.wizardry.spells.modules.ModuleType;

public class ModuleSilent extends Module
{
	private Module[] modules;
	
	public ModuleSilent(Module... modules)
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