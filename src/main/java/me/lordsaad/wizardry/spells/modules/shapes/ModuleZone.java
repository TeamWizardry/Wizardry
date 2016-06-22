package me.lordsaad.wizardry.spells.modules.shapes;

import me.lordsaad.wizardry.api.modules.IModule;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleZone implements IModule {
    private IModule[] modules;

    public ModuleZone(IModule... modules) {
        this.modules = modules;
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