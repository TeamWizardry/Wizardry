package com.teamwizardry.wizardry.common.spell.component;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.ISpellComponent;
import com.teamwizardry.wizardry.configs.ServerConfigs;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ComponentRegistry
{
    private static final Map<String, Module> modules = new HashMap<>();
    private static final Map<String, Modifier> modifiers = new HashMap<>();
    private static final Map<List<Item>, ISpellComponent> spellComponents = new HashMap<>();
    
    private static TargetComponent entityTarget;
    private static TargetComponent blockTarget;
    
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
        List<Item> items = component.getItems();
        for (List<Item> keys : spellComponents.keySet())
        {
            if (listStartsWith(keys, items))
            {
                Wizardry.LOGGER.warn("Spell component registration failed for {} {}, recipe hidden by {}",
                        component.getClass().getSimpleName(), component.getName(), spellComponents.get(keys).getName());
                return false;
            }
        }
        map.put(component.getName(), component);
        spellComponents.put(component.getItems(), component);
        return true;
    }
    
    public static void loadTargets()
    {
        if (entityTarget != null)
            spellComponents.remove(entityTarget.getItems());
        if (blockTarget != null)
            spellComponents.remove(blockTarget.getItems());
        
        entityTarget = new TargetComponent("entityTarget", ForgeRegistries.ITEMS.getValue(new ResourceLocation(ServerConfigs.entityTargetItem)));
        blockTarget = new TargetComponent("blockTarget", ForgeRegistries.ITEMS.getValue(new ResourceLocation(ServerConfigs.blockTargetItem)));
        
        spellComponents.put(entityTarget.getItems(), entityTarget);
        spellComponents.put(blockTarget.getItems(), blockTarget);
    }
    
    public static TargetComponent getEntityTarget() { return entityTarget; }
    public static TargetComponent getBlockTarget() { return blockTarget; }
    public static Map<String, Module> getModules() {
        return modules;
    }
}
