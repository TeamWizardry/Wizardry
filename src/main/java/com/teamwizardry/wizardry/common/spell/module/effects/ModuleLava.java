package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 6/21/2016.
 */
public class ModuleLava extends Module {
    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Places a lava source block at the targeted location.";
    }

    @Override
    public String getDisplayName() {
        return "Lava";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    @Override
    public boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell) {
        // TODO Auto-generated method stub
    	return false;
    }
}