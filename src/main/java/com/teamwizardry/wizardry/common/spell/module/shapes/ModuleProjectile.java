package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleProjectile extends Module {
    public ModuleProjectile() {
        attributes.addAttribute(Attribute.SPEED);
        attributes.addAttribute(Attribute.PIERCE);
        attributes.addAttribute(Attribute.SCATTER);
        attributes.addAttribute(Attribute.PROJ_COUNT);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }

    @Override
    public String getDescription()
    {
    	return "Fires a projectile that targets the first entity hit.";
    }

    @Override
    public String getDisplayName() {
        return "Projectile";
    }

    @Override
    public NBTTagCompound getModuleData() {
    	NBTTagCompound compound = super.getModuleData();
    	compound.setDouble(SPEED, attributes.apply(Attribute.SPEED, 1));
    	compound.setDouble(PIERCE, attributes.apply(Attribute.PIERCE, 0));
    	compound.setDouble(SCATTER, attributes.apply(Attribute.SCATTER, 0));
    	compound.setInteger(PROJ_COUNT, (int) attributes.apply(Attribute.PROJ_COUNT, 1));
    	compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
    	compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return null;
    }

	@Override
	public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell)
	{
		// TODO Auto-generated method stub
		return false;
	}
}