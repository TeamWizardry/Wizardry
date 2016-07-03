package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleSelf extends Module {
    public ModuleSelf() {
    	attributes.addAttribute(Attribute.DURATION);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setInteger(DURATION, (int) attributes.apply(Attribute.DURATION, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 5));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 5));
    	return null;
    }

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}
}