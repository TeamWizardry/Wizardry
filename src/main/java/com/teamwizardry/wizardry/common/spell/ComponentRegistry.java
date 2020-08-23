package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.ISpellComponent;

import net.minecraft.item.Item;

public class ComponentRegistry
{
    private static final Map<String, Module> modules = new HashMap<>();
    private static final Map<String, Modifier> modifiers = new HashMap<>();
    
    private static final Map<List<Item>, ISpellComponent> spellComponents = new HashMap<>();
    
    /**
     * Unconstructible
     */
    private ComponentRegistry() {}
    
    public static void addModule(Module module)
    {
        tryRegister(module, modules);
    }
    
    public static void addModifier(Modifier modifier)
    {
        tryRegister(modifier, modifiers);
    }

    public static ISpellComponent getComponentForItems(List<Item> items)
    {
        for (List<Item> spells : spellComponents.keySet())
            if (listStartsWith(items, spells))
                return spellComponents.get(spells);
        return null;
    }
    
    private static boolean listStartsWith(List<Item> list, List<Item> other)
    {
        if (other.size() > list.size())
            return false;
        
        ListIterator<Item> listIter = list.listIterator();
        ListIterator<Item> otherIter = other.listIterator();
        while (listIter.hasNext() && otherIter.hasNext())
            if (!listIter.next().equals(otherIter.next()))
                return false;
        return true;
    }
    
    private static <Component extends ISpellComponent> boolean tryRegister(Component component, Map<String, ? super Component> map)
    {
        List<Item> item = component.getItems();
        if (spellComponents.containsKey(item))
        {
            Wizardry.LOGGER.warn("Spell component registration failed for {} {}, Item {} already linked to {}",
                    component.getClass().getSimpleName(), component.getName(), item, spellComponents.get(item));
            return false;
        }
        map.put(component.getName(), component);
        spellComponents.put(component.getItems(), component);
        return true;
    }
}
