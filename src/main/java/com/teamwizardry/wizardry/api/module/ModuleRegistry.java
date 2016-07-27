package com.teamwizardry.wizardry.api.module;

import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
/*
    @author eladkay
*/
public class ModuleRegistry {
    private int id = 0;
    private static ModuleRegistry theModuleRegistry = new ModuleRegistry();

    public static ModuleRegistry getInstance() {
        return theModuleRegistry;
    }

    public SortedMap<Integer, Module> booleanItems;
    public SortedMap<Integer, Module> effectItems;
    public SortedMap<Integer, Module> eventItems;
    public SortedMap<Integer, Module> modifierItems;
    public SortedMap<Integer, Module> shapeItems;
    public Map<Integer, IModuleConstructor> modules;

    public Module createModule(ItemStack stack, ModuleType type) {
        SortedMap<Integer, Module> map;
        switch (type) {
            case BOOLEAN:
                map = booleanItems;
                break;
            case EFFECT:
                map = effectItems;
                break;
            case EVENT:
                map = eventItems;
                break;
            case MODIFIER:
                map = modifierItems;
                break;
            case SHAPE:
                map = shapeItems;
                break;
            default:
                return null;
        }
        for (Module test : map.values())
            if(test.stack == null)
                System.err.println("Module " + test.getDisplayName() + " has a null stack!");
            else if (test.stack.equals(stack)) //y'know, you can't equals stacks. todo
                return test;
        return null;
    }

    @FunctionalInterface
    public interface IModuleConstructor {
        Module construct(ItemStack stack);
    }

    private ModuleRegistry() {
        modules = new HashMap<>();
        booleanItems = new TreeMap<>();
        effectItems = new TreeMap<>();
        eventItems = new TreeMap<>();
        modifierItems = new TreeMap<>();
        shapeItems = new TreeMap<>();


    }

    public Pair<Integer, Module> registerModule(IModuleConstructor module, ItemStack stack) {
        switch (module.construct(stack).getType()) {
            case BOOLEAN:
                booleanItems.putIfAbsent(id++, module.construct(stack));
                break;
            case EFFECT:
                effectItems.putIfAbsent(id++, module.construct(stack));
                break;
            case EVENT:
                eventItems.putIfAbsent(id++, module.construct(stack));
                break;
            case MODIFIER:
                modifierItems.putIfAbsent(id++, module.construct(stack));
                break;
            case SHAPE:
                shapeItems.putIfAbsent(id++, module.construct(stack));
                break;
        }
        modules.putIfAbsent(id, module);
        return new Pair<>(id, module.construct(stack));
    }

    public Module getModuleById(int id) {
        if (booleanItems.get(id) != null) return booleanItems.get(id);
        if (effectItems.get(id) != null) return effectItems.get(id);
        if (eventItems.get(id) != null) return eventItems.get(id);
        if (modifierItems.get(id) != null) return modifierItems.get(id);
        if (shapeItems.get(id) != null) return shapeItems.get(id);
        return null;
    }

    public static class Pair<T, V> {
        public T t;
        public V v;

        public Pair(T t, V v) {
            this.t = t;
            this.v = v;
        }
    }
}
