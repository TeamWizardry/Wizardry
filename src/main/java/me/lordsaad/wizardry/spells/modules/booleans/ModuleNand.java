package me.lordsaad.wizardry.spells.modules.booleans;

import me.lordsaad.wizardry.api.modules.IModule;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ModuleNand implements IModule {
    private IModule[] modules;

    public ModuleNand(IModule... modules) {
        this.modules = modules;
    }

    @Override
    public ModuleType getType() {
        return ModuleType.BOOLEAN;
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Type", "OR");

        NBTTagList list = new NBTTagList();
        for (IModule module : modules)
            list.appendTag(module.getModuleData());
        compound.setTag("Modules", list);
        return compound;
    }
}