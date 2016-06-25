package com.teamwizardry.wizardry.spells.modules.effects;

import com.teamwizardry.wizardry.api.modules.Module;
import com.teamwizardry.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleFallProtection extends Module {
    private int protectionLevel;

    public ModuleFallProtection() {

    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setInteger(POWER, protectionLevel);
        return compound;
    }

    public ModuleFallProtection setProtectionLevel(int level) {
        protectionLevel = level;
        return this;
    }
}