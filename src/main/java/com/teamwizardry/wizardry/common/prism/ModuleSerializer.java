package com.teamwizardry.wizardry.common.prism;

import java.util.Map.Entry;

import com.teamwizardry.librarianlib.prism.nbt.NBTSerializer;
import com.teamwizardry.wizardry.common.spell.component.ComponentRegistry;
import com.teamwizardry.wizardry.common.spell.component.Module;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;

public class ModuleSerializer extends NBTSerializer<Module>
{
    private static final ModuleSerializer INSTANCE = new ModuleSerializer();
    private ModuleSerializer() {}
    public static ModuleSerializer get() { return INSTANCE; }
    
    @Override
    protected Module deserialize(INBT nbt, Module module)
    {
        return ComponentRegistry.getModules().getOrDefault(nbt.getString(), module);
    }

    @Override
    protected INBT serialize(Module module)
    {
        return StringNBT.valueOf(ComponentRegistry.getModules().entrySet().stream().filter(entry -> entry.getValue().equals(module)).map(Entry::getKey).findFirst().get());
    }
}
