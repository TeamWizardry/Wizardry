package com.teamwizardry.wizardry.spells.modules.effects;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.spells.modules.ModuleType;
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
        return compound;
    }

    public ModuleExplosion setDamageTerrain(boolean canDamageTerrain) {
        damageTerrain = canDamageTerrain;
        return this;
    }
}