package com.teamwizardry.wizardry.api.module;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;

/*
    @author eladkay
*/
public class ModuleRegistry {

    private final static ModuleRegistry INSTANCE = new ModuleRegistry();
    private Map<ModuleType, Map<ResourceLocation, Module>> modules = Maps.newHashMap();
    private BiMap<ResourceLocation, Module> moduleLookup = HashBiMap.create(512);

    private ModuleRegistry() {
        for (ModuleType type : ModuleType.values()) modules.putIfAbsent(type, HashBiMap.create(512));
    }

    public static ModuleRegistry getInstance() {
        return INSTANCE;
    }

    public static boolean areItemsEqual(ItemStack stack, ItemStack stack2) {
        if (stack.getItem() != stack2.getItem()) return false;
        if (stack.getItemDamage() != OreDictionary.WILDCARD_VALUE)
            if (stack2.getItemDamage() != OreDictionary.WILDCARD_VALUE)
                if (stack.getItemDamage() != stack2.getItemDamage()) return false;
        return true;
    }

    public static boolean areItemStacksEqual(ItemStack stack, ItemStack stack2) {
        if (stack.getItem() != stack2.getItem()) return false;
        if (stack.getItemDamage() != OreDictionary.WILDCARD_VALUE)
            if (stack2.getItemDamage() != OreDictionary.WILDCARD_VALUE)
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

    public int getRegistrySize() {
        return moduleLookup.size();
    }

    public BiMap<ResourceLocation, Module> getRegistryMap() {
        return moduleLookup;
    }

    public Module getModuleFromItemStack(ItemStack stack, ModuleType type) {
        if (type != null)
            for (ResourceLocation rl : modules.get(type).keySet())
                if (areItemsEqual(modules.get(type).get(rl).stack, stack)) return modules.get(type).get(rl);
        return null;
    }

    public Pair<ResourceLocation, Module> registerModule(IModuleConstructor module, ItemStack stack) {
        Module constructedModule = module.construct(stack);
        ResourceLocation location = constructedModule.getResourceLocation();
        ModuleType type = constructedModule.getType();
        modules.get(type).putIfAbsent(location, constructedModule);
        moduleLookup.put(location, constructedModule);
        Wizardry.logger.info("Registered " + constructedModule.getDisplayName() + " in " + type + " under " + location);
        return new Pair<>(location, constructedModule);
    }

    public Module getModuleByLocation(String location) {
        return moduleLookup.get(new ResourceLocation(location));
    }

    public ResourceLocation getModuleLocation(Module module) {
        return moduleLookup.inverse().get(module);
    }

    public Map<ModuleType, Map<ResourceLocation, Module>> getModules() {
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
