package com.teamwizardry.wizardry.spells.modules.booleans;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleNand extends Module {
    public ModuleNand() {

    }

    @Override
    public ModuleType getType() {
        return ModuleType.BOOLEAN;
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString(CLASS, "NAND");
        return compound;
    }
}