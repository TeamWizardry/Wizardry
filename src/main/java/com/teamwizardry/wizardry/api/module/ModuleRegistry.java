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
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/*
    @author eladkay
*/
public class ModuleRegistry {

	private static final ModuleRegistry INSTANCE = new ModuleRegistry();
	private final Map<ModuleType, Map<ResourceLocation, Module>> modules = Maps.newHashMap();
	private final BiMap<ResourceLocation, Module> moduleLookup = HashBiMap.create(512);

	private ModuleRegistry() {
		for (ModuleType type : ModuleType.values()) modules.putIfAbsent(type, HashBiMap.create(512));
	}

	public static ModuleRegistry getInstance() {
		return INSTANCE;
	}

	public int getRegistrySize() {
		return moduleLookup.size();
	}

	public BiMap<ResourceLocation, Module> getRegistryMap() {
		return moduleLookup;
	}

	@Nullable
	public Module getModuleFromItemStack(ItemStack stack, ModuleType type) {
		if (type != null)
			for (ResourceLocation rl : modules.get(type).keySet())
				if (ItemStack.areItemStacksEqual(modules.get(type).get(rl).stack, stack)) return modules.get(type).get(rl);
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
