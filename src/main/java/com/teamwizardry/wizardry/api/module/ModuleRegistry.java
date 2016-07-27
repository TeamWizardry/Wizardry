package com.teamwizardry.wizardry.api.module;

import com.google.common.collect.Maps;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.LinkedHashMap;

/*
    @author eladkay
*/
public class ModuleRegistry {

    private final static ModuleRegistry INSTANCE = new ModuleRegistry();
    private int id = 0;
    private HashMap<ModuleType, LinkedHashMap<Integer, Module>> modules = Maps.newHashMap();

    private ModuleRegistry() {
        for (ModuleType type : ModuleType.values()) modules.putIfAbsent(type, new LinkedHashMap<>());
    }

    public static ModuleRegistry getInstance() {
        return INSTANCE;
    }

    private static boolean areItemStacksEqual(ItemStack stack, ItemStack stack2) {
        if (stack.getItem() != stack2.getItem()) return false;
        if (stack.getItemDamage() != stack2.getItemDamage()) return false;
        if (stack.stackSize != stack2.stackSize) return false;
        NBTTagCompound compound1 = stack.getTagCompound();
        NBTTagCompound compound2 = stack2.getTagCompound();
        if (compound1 == null && compound2 != null) return false;
        if (compound1 != null && compound2 == null) return false;

        if (compound1 != null) {
            for (String tag1 : compound1.getKeySet()) {
                boolean success = false;
                for (String tag2 : compound2.getKeySet()) {
                    if (tag1.equals(tag2)) {
                        success = true;
                        break;
                    }
                }
                if (!success) return false;
            }
            for (String tag2 : compound2.getKeySet()) {
                boolean success = false;
                for (String tag1 : compound1.getKeySet()) {
                    if (tag2.equals(tag1)) {
                        success = true;
                        break;
                    }
                }
                if (!success) return false;
            }
        }
        return true;
    }

    public Module getModuleFromItemStack(ItemStack stack) {
        for (ModuleType type : ModuleType.values())
            for (int ID : modules.get(type).keySet())
                if (areItemStacksEqual(modules.get(type).get(ID).stack, stack)) return modules.get(type).get(ID);
        return null;
    }

    public Pair<Integer, Module> registerModule(IModuleConstructor module, ItemStack stack) {
        Module constructedModule = module.construct(stack);
        modules.get(constructedModule.getType()).putIfAbsent(++id, constructedModule);
        return new Pair<>(id, module.construct(stack));
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
