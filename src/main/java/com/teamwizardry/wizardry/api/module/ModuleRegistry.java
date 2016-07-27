package com.teamwizardry.wizardry.api.module;

import com.google.common.collect.Maps;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;

/*
    @author eladkay
*/
public class ModuleRegistry {

    private static ModuleRegistry INSTANCE = new ModuleRegistry();
    private int id = 0;
    private HashMap<ModuleType, LinkedHashMap<Integer, Module>> modules = Maps.newHashMap();
    private ModuleRegistry() {
        for (ModuleType type : ModuleType.values()) modules.putIfAbsent(type, new LinkedHashMap<>());
    }

    public static ModuleRegistry getInstance() {
        return INSTANCE;
    }

    // TODO: stack.equals will not match "stack". Required: Itemstack matching method
    public Module getModuleFromItemStack(ItemStack stack) {
        for (ModuleType type : ModuleType.values())
            for (int ID : modules.get(type).keySet())
                if (modules.get(type).get(ID).stack.equals(stack)) return modules.get(type).get(ID);
        return null;
    }

    public Pair<Integer, Module> registerModule(IModuleConstructor module, ItemStack stack) {
        return new Pair<>(++id, module.construct(stack));
    }

    public Module getModuleById(int id) {
        for (ModuleType moduleType : ModuleType.values())
            for (int ID : modules.get(moduleType).keySet())
                if (id == ID) return modules.get(moduleType).get(ID);
        return null;
    }

    public HashMap<ModuleType, LinkedHashMap<Integer, Module>> getModules() {
        return modules;
    }

    @FunctionalInterface
    public interface IModuleConstructor {
        Module construct(ItemStack stack);
    }

    public static class Pair<T, V> {
        public T t;
        public V v;

        Pair(T t, V v) {
            this.t = t;
            this.v = v;
        }
    }
}
