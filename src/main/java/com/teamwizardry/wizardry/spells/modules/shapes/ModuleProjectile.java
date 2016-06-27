package com.teamwizardry.wizardry.spells.modules.shapes;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.api.modules.attribute.Attribute;
import com.teamwizardry.wizardry.spells.modules.ModuleType;
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
    public NBTTagCompound getModuleData() {
        return null;
    }
}