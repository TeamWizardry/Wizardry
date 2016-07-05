package com.teamwizardry.wizardry.common.spell.module.events;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleBlinkEvent extends Module {
    @Override
    public ModuleType getType() {
        return ModuleType.EVENT;
    }
    
    @Override
    public String getDescription()
    {
    	return "Called whenever a targetable entity blinks.";
    }

    @Override
    public String getDisplayName() {
        return "If Target Blinks";
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		return false;
	}
}