package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.teamwizardry.wizardry.api.spell.Modifier;
import com.teamwizardry.wizardry.api.spell.Module;

public class ComponentRegistry
{
    private static final Map<String, Module> modules = new HashMap<>();
    private static final Set<Modifier> modifiers = new HashSet<>();
    
    private static final HashMap<Module, List<Modifier>> completeModifiers = new HashMap<>(); // This module uses all of the attributes in this modifier
    private static final HashMap<Module, List<Modifier>> incompleteModifiers = new HashMap<>(); // This module uses some, but not all, of the attributes in this modifier
    
    /**
     * Unconstructible
     */
    private ComponentRegistry() {}
    
    public static void addModule(Module module)
    {
        modules.put(module.getPattern().getRegistryName() + ":" + module.getName(), module);
    }
    
    public static void addModifier(Modifier modifier)
    {
        modifiers.add(modifier);
    }
}
