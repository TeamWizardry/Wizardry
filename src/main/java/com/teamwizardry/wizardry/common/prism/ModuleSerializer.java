package com.teamwizardry.wizardry.common.prism;

import java.util.Map.Entry;

import com.teamwizardry.librarianlib.scribe.nbt.NbtSerializer;
import com.teamwizardry.wizardry.common.spell.component.ComponentRegistry;
import com.teamwizardry.wizardry.common.spell.component.Module;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

public class ModuleSerializer extends NbtSerializer<Module>
{
    private static final ModuleSerializer INSTANCE = new ModuleSerializer();
    private ModuleSerializer() {}
    public static ModuleSerializer get() { return INSTANCE; }
    
    @Override
    protected Module deserialize(NbtElement nbt)
    {
        return ComponentRegistry.getModules().get(nbt.asString());
    }

    @Override
    protected NbtElement serialize(Module module)
    {
        return NbtString.of(ComponentRegistry.getModules().entrySet().stream().filter(entry -> entry.getValue().equals(module)).map(Entry::getKey).findFirst().get());
    }
}
