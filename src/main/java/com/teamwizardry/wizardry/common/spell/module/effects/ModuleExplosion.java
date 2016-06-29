package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleExplosion extends Module {
    private static final String DAMAGE_TERRAIN = "Damage Terrain";

    private boolean damageTerrain;

    public ModuleExplosion() {
        attributes.addAttribute(Attribute.DAMAGE);
        attributes.addAttribute(Attribute.POWER);
        attributes.addAttribute(Attribute.RADIUS);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Cause an explosion dealing blast damage. More increases the size and strength. x64 deals terrain damage.";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setBoolean(DAMAGE_TERRAIN, damageTerrain);
        
        compound.setDouble(DAMAGE, attributes.apply(Attribute.DAMAGE, 1));
        compound.setDouble(POWER, attributes.apply(Attribute.POWER, 1));
        compound.setDouble(RADIUS, attributes.apply(Attribute.RADIUS, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    public ModuleExplosion setDamageTerrain(boolean canDamageTerrain) {
        damageTerrain = canDamageTerrain;
        return this;
    }
}