package me.lordsaad.wizardry.spells.modules.events;

import me.lordsaad.wizardry.api.modules.IModule;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleBlinkEvent implements IModule {
    private IModule[] modules;

    public ModuleBlinkEvent(IModule... modules) {
        this.modules = modules;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EVENT;
    }

    @Override
    public NBTTagCompound getModuleData() {
        return null;
    }
}