package com.teamwizardry.wizardry.common.spell.module.events;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleFallEvent extends Module {
    public ModuleFallEvent(ItemStack stack) {
        super(stack);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EVENT;
    }
    
    @Override
    public String getDescription()
    {
    	return "Called whenever a targetable entity hits the ground.";
    }

    @Override
    public String getDisplayName() {
        return "If Target Takes Fall Damage";
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		return false;
	}
}