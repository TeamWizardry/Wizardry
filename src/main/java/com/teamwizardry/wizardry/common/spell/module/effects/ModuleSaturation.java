package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
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
    public NBTTagCompound getModuleData() {
        return null;
    }
}