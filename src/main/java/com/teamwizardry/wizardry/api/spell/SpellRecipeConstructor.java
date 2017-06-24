package com.teamwizardry.wizardry.api.spell;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.*;

/**
 * Created by LordSaad.
 */
public class SpellRecipeConstructor {

	public ArrayList<ItemStack> finalList = new ArrayList<>();
	private HashMap<Integer, ArrayList<Module>> moduleHeads;

	// TODO: prune similar modules in fields to make spells cheaper
	public SpellRecipeConstructor(HashMap<Integer, ArrayList<Module>> moduleHeads) {
		this.moduleHeads = moduleHeads;
		ArrayDeque<Item> identifiers = new ArrayDeque<>(SpellStack.identifiers);
		HashBiMap<Module, ItemStack> baseIngredients = HashBiMap.create();
		HashMultimap<Module, ModuleModifier> modifiers = HashMultimap.create();

		Module lastModule = null;
		for (int i : moduleHeads.keySet())
			for (Module module : SpellStack.getAllModules(moduleHeads.get(i))) {
				if (identifiers.isEmpty()) break;
				if (lastModule == null) lastModule = module;

				if (module instanceof ModuleModifier) {
					modifiers.put(lastModule, (ModuleModifier) module);
				} else {
					lastModule = module;
					baseIngredients.put(module, new ItemStack(identifiers.poll()));
				}
			}

		for (Module module : baseIngredients.keySet()) {
			finalList.add(baseIngredients.get(module));
			finalList.add(module.getItemStack());
			if (modifiers.containsKey(module)) {
				Set<ModuleModifier> moduleModifiers = modifiers.get(module);
				for (ModuleModifier moduleModifier : moduleModifiers) {
					ItemStack lastStack = finalList.get(finalList.size() - 1);
					if (lastStack.getItem() == moduleModifier.getItemStack().getItem()) {
						lastStack.setCount(lastStack.getCount() + 1);
					} else finalList.add(moduleModifier.getItemStack());
				}
			}
			finalList.add(new ItemStack(SpellStack.fieldLineBreak));
		}

		finalList.add(new ItemStack(SpellStack.fieldCodeSplitter));

		for (int i : moduleHeads.keySet()) {
			for (Module module2 : SpellStack.getAllModules(moduleHeads.get(i))) {
				if (module2 instanceof ModuleModifier) continue;
				finalList.add(baseIngredients.get(module2));
			}
			finalList.add(new ItemStack(SpellStack.codeLineBreak));
		}
		finalList.add(new ItemStack(ModItems.PEARL_NACRE));

		System.out.println(finalList);
	}

	private ArrayList<Module> newPrune(ArrayList<Module> modules) {
		System.out.println("-------------------------------------------------------------");
		System.out.println(modules);
		System.out.println("------------");
		ArrayList<Module> dedup = removeDuplicates(modules);
		System.out.println(dedup);
		return dedup;
	}

	private boolean compareModules(Module module1, Module module2) {
		if (module2.equals(module1)) return true;

		System.out.println(module1.getID() + " -<<  " + module1.modifiers);
		System.out.println(module2.getID() + " <<-  " + module2.modifiers);
		List<AttributeModifier> modifiers1 = new ArrayList<>(module1.modifiers);
		List<AttributeModifier> modifiers2 = new ArrayList<>(module2.modifiers);
		modifiers1.removeIf(modifiers2::contains);
		System.out.println(module1.getID() + " ->>  " + modifiers1);
		System.out.println("======================================");

		return module1.getID().equals(module2.getID()) && modifiers1.isEmpty();
	}

	private ArrayList<Module> removeDuplicates(ArrayList<Module> list) {
		ArrayList<Module> deduplicated = new ArrayList<>();
		main:
		for (Module module : list) {
			for (Module checkAgainst : deduplicated) {
				if (compareModules(module, checkAgainst)) {
					continue main;
				}
			}
			deduplicated.add(module);
		}
		return deduplicated;
	}

	public JsonObject getRecipeJson() {
		if (finalList.isEmpty()) return null;
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();

		for (ItemStack stack : finalList) {
			ResourceLocation location = stack.getItem().getRegistryName();
			if (location == null) continue;
			array.add(new JsonPrimitive(location.toString()));
		}

		object.add("list", array);

		Module lastModule = null;
		StringBuilder finalName = null;
		for (int i : moduleHeads.keySet())
			for (Module module : moduleHeads.get(i)) {
				if (lastModule == null) lastModule = module;
				if (module != null) {
					Module tempModule = module;
					while (tempModule != null) {

						boolean next = false;
						if (lastModule != module) {
							lastModule = module;
							finalName.append(" || ");
							next = true;
						}

						if (finalName == null) finalName = new StringBuilder(tempModule.getReadableName());
						else {
							if (!next) finalName.append(" -> ");
							finalName.append(tempModule.getReadableName());
						}

						tempModule = tempModule.nextModule;
					}
				}
			}
		if (finalName != null) {
			object.addProperty("name", finalName.toString());
		}

		return object;
	}
}
