package com.teamwizardry.wizardry.spells.modules.effects;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleLight extends Module {
    public ModuleLight() {
        attributes.addAttribute(Attribute.DURATION);
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