package com.teamwizardry.wizardry.common.spell.module.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;

/**
 * Created by Saad on 6/21/2016.
 */
public class ModuleMeleeEvent extends Module
{
    @Override
    public ModuleType getType() {
        return ModuleType.EVENT;
    }

    @Override
    public String getDescription()
    {
    	return "Called whenever a targetable entity is struck by a melee attack.";
    }

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}
}
