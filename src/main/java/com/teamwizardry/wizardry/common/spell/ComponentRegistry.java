package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.ISpellComponent;
import com.teamwizardry.wizardry.api.spell.Modifier;
import com.teamwizardry.wizardry.api.spell.Module;

import net.minecraft.item.Item;

public class ComponentRegistry
{
    private static final Map<String, Module> modules = new HashMap<>();
    private static final Set<Modifier> modifiers = new HashSet<>();
    
    private static final Map<Module, List<Modifier>> relevantModifiers = new HashMap<>(); // This module uses all of the attributes in this modifier
    
    private static final Map<Item, ISpellComponent> spellComponents = new HashMap<>();
    
    /**
     * Unconstructible
     */
    private ComponentRegistry() {}
    
    public static void addModule(Module module)
    {
        Item item = module.getItem();
        if (spellComponents.containsKey(item))
            Wizardry.LOGGER.warn("Module registration failed for Module {}, Item {} already linked to {}", 
                    module.getName(), item, spellComponents.get(item));
        
        modules.put(module.getPattern().getRegistryName() + ":" + module.getName(), module);
        spellComponents.put(item, module);
    }
    
    public static void addModifier(Modifier modifier)
    {
        Item item = modifier.getItem();
        if (spellComponents.containsKey(item))
            Wizardry.LOGGER.warn("Module registration failed for Modifier {}, Item {} already linked to {}", 
                    modifier.getName(), item, spellComponents.get(item));
        
        modifiers.add(modifier);
        spellComponents.put(item, modifier);
        Set<String> attributes = modifier.getAffectedAttributes();
        
        modules.values().stream().filter(module -> module.getAttributeRanges()
                                                         .keySet()
                                                         .containsAll(attributes))
                                 .forEach(module -> relevantModifiers.computeIfAbsent(module, m -> new LinkedList<>())
                                                                     .add(modifier));
    }
    
    public static List<Modifier> getRelevantModifiers(String modifier)
    {
        return relevantModifiers.getOrDefault(modules.get(modifier), new LinkedList<>());
    }
    
    public static ISpellComponent getComponentForItem(Item item)
    {
        return spellComponents.get(item);
    }
}
