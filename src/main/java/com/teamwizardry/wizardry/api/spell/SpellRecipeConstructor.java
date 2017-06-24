package com.teamwizardry.wizardry.api.spell;

import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.init.ModItems;
import kotlin.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by LordSaad.
 */
public class SpellRecipeConstructor {

	public ArrayList<ItemStack> finalList = new ArrayList<>();;
	private HashSet<Module> moduleHeads;

	public SpellRecipeConstructor(HashSet<Module> moduleHeads) {
		this.moduleHeads = moduleHeads;
		HashBiMap<Module, Pair<ItemStack, ItemStack>> baseIngredients = HashBiMap.create();
		ArrayDeque<Item> identifiers = new ArrayDeque<>();
		identifiers.addAll(SpellStack.identifiers);

		ArrayList<Module> temp = new ArrayList<>();
		temp.addAll(moduleHeads);
		ArrayList<Module> prunedModules = prune(temp);

		for (Module module : prunedModules) {
			if (module instanceof IModifier); // TODO

			if (identifiers.isEmpty()) break;
			ItemStack identifier = new ItemStack(identifiers.poll());
			baseIngredients.put(module, new Pair<>(identifier, module.getItemStack().copy())); // Add the itemstack of each module to baseIngredients
		}

		// Add all the fileds
		for (Module module : baseIngredients.keySet()) {
			Pair<ItemStack, ItemStack> pair = baseIngredients.get(module);
			if (pair == null) continue;
			finalList.add(pair.getFirst());
			finalList.add(pair.getSecond());
			finalList.add(new ItemStack(SpellStack.fieldLineBreak));
		}
		finalList.add(new ItemStack(SpellStack.fieldCodeSplitter));

		// Add the code lines.
		for (Module head : moduleHeads) {
			for (Module module : SpellStack.getAllModules(head)) {

				Pair<ItemStack, ItemStack> pair = null;
				for (Module module1 : baseIngredients.keySet()) {
					if (module1.getID().equals(module.getID())) {
						pair = baseIngredients.get(module1);
						break;
					}
				}

				if (pair == null) continue;
				finalList.add(pair.getFirst());
			}
			finalList.add(new ItemStack(SpellStack.codeLineBreak));
		}

		finalList.add(new ItemStack(ModItems.PEARL_NACRE));
	}

	private ArrayList<Module> prune(ArrayList<Module> modules) {
		ArrayList<Module> prunedModules = new ArrayList<>();
		for (Module head : modules) {
			prunedModules.addAll(SpellStack.getAllModules(head));
		}

		ArrayList<Module> temp = new ArrayList<>();
		temp.addAll(prunedModules);
		for (Module module : temp) {
			for (Module module1 : temp) {
				if (module != module1 && module.getID().equals(module1.getID())) {
					prunedModules.remove(module);
					return prune(prunedModules);
				}
			}
		}
		return prunedModules;
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
		for (Module module : moduleHeads) {
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
