package com.teamwizardry.wizardry.common.spell.module.booleans;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleAnd extends Module {

    @Override
    public ModuleType getType() {
        return ModuleType.BOOLEAN;
    }

    @Override
    public String getDescription() {
        return "Will pass conditions if all are true.";
    }

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}

    @Override
    public String getDisplayName() {
        return "And";
    }
}
