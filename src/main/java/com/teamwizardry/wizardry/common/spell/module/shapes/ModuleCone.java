package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleCone extends Module {
    public ModuleCone() {
        attributes.addAttribute(Attribute.DISTANCE);
        attributes.addAttribute(Attribute.SCATTER);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.SHAPE;
    }

    @Override
    public NBTTagCompound getModuleData() {
        return null;
    }
}