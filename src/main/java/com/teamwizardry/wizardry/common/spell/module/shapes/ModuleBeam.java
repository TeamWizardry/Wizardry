package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleBeam extends Module {
    public ModuleBeam() {
        attributes.addAttribute(Attribute.DISTANCE);
        attributes.addAttribute(Attribute.SCATTER);
        attributes.addAttribute(Attribute.PROJ_COUNT);
        attributes.addAttribute(Attribute.PIERCE);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }
    
    @Override
    public String getDescription()
    {
    	return "Casts a beam that strikes the first target in a raycast.";
    }

    @Override
    public String getDisplayName() {
        return "Beam";
    }

    @Override
    public NBTTagCompound getModuleData() {
    	NBTTagCompound compound = super.getModuleData();
    	compound.setDouble(DISTANCE, attributes.apply(Attribute.DISTANCE, 1));
    	compound.setDouble(SCATTER, attributes.apply(Attribute.SCATTER, 0));
    	compound.setInteger(PROJ_COUNT, (int) attributes.apply(Attribute.PROJ_COUNT, 1));
    	compound.setInteger(PIERCE, (int) attributes.apply(Attribute.PIERCE, 0));
        return null;
    }

	@Override
	public void cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		
	}
}