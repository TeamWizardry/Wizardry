package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleFlame extends Module {
    public ModuleFlame() {
        attributes.addAttribute(Attribute.DURATION);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "Inflict fire damage for 1 tick. More will increase fire ticks. Will smelt block it touches.";
    }

    @Override
    public NBTTagCompound getModuleData() {
        return null;
    }
}