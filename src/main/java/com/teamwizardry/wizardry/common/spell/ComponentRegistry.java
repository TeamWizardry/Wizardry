package com.teamwizardry.wizardry.common.spell;

import java.util.HashMap;
import java.util.List;
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

    public static ISpellComponent getComponentForItem(List<Item> items)
    {
        return spellComponents.get(items);
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
