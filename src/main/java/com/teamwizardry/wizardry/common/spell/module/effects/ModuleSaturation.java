package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleSaturation extends Module {
    public ModuleSaturation() {
        attributes.addAttribute(Attribute.POWER);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Give the targeted player hunger and saturation points.";
    }
    
    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setInteger(POWER, (int) attributes.apply(Attribute.POWER, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
    	return compound;
    }

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}
}