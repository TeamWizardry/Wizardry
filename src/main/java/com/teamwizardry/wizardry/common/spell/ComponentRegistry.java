package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.BlockTarget;
import com.teamwizardry.wizardry.api.spell.EntityTarget;
import com.teamwizardry.wizardry.api.spell.ISpellComponent;

import net.minecraft.item.Item;

public class ComponentRegistry
{
    private static final Map<String, Module> modules = new HashMap<>();
    private static final Map<String, EntityTarget> entityTargets = new HashMap<>();
    private static final Map<String, BlockTarget> blockTargets = new HashMap<>();
    private static final Map<String, Element> elements = new HashMap<>();
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
        {
            logRegistrationError(module.getClass().getSimpleName(), module.getName(), item, spellComponents.get(item));
            return;
        }
        
        modules.put(module.getPattern().getRegistryName() + ":" + module.getName(), module);
        spellComponents.put(item, module);
    }
    
    public static void addElement(Element element)
    {
        Item item = element.getItem();
        if (spellComponents.containsKey(item))
        {
            logRegistrationError(element.getClass().getSimpleName(), element.getName(), item, spellComponents.get(item));
            return;
        }
        
        elements.put(element.getName(), element);
        spellComponents.put(item, element);
    }
    
    public static void addModifier(Modifier modifier)
    {
        Item item = modifier.getItem();
        if (spellComponents.containsKey(item))
        {
            logRegistrationError(modifier.getClass().getSimpleName(), modifier.getName(), item, spellComponents.get(item));
            return;
        }
        
        modifiers.add(modifier);
        spellComponents.put(item, modifier);
        modules.values().stream().forEach(module -> relevantModifiers.computeIfAbsent(module, m -> new LinkedList<>())
                                                                     .add(modifier));
    }
    
    public static void addEntityTarget(EntityTarget target)
    {
        Item item = target.getItem();
        if (spellComponents.containsKey(item))
        {
            logRegistrationError(target.getClass().getSimpleName(), target.getName(), item, spellComponents.get(item));
            return;
        }
        
        entityTargets.put(target.getName(), target);
        spellComponents.put(item, target);
    }
    
    public static void addBlockTarget(BlockTarget target)
    {
        Item item = target.getItem();
        if (spellComponents.containsKey(item))
        {
            logRegistrationError(target.getClass().getSimpleName(), target.getName(), item, spellComponents.get(item));
            return;
        }
        
        blockTargets.put(target.getName(), target);
        spellComponents.put(item, target);
    }
    
    public static List<Modifier> getRelevantModifiers(String modifier)
    {
        return relevantModifiers.getOrDefault(modules.get(modifier), new LinkedList<>());
    }
    
    public static ISpellComponent getComponentForItem(Item item)
    {
        return spellComponents.get(item);
    }
    
    public static EntityTarget getEntityTarget(String name)
    {
        return entityTargets.get(name);
    }
    
    public static BlockTarget getBlockTarget(String name)
    {
        return blockTargets.get(name);
    }

    private static void logRegistrationError(Object... args)
    {
        Wizardry.LOGGER.warn("Spell component registration failed for {} {}, Item {} already linked to {}", args);
    }
}
