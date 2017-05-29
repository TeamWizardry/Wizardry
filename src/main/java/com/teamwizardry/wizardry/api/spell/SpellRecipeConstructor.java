package com.teamwizardry.wizardry.api.spell;

import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teamwizardry.wizardry.init.ModItems;
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

	public ArrayList<ItemStack> finalList = new ArrayList<>();
	private HashSet<Module> moduleHeads;

	public SpellRecipeConstructor(HashSet<Module> moduleHeads) {
		this.moduleHeads = moduleHeads;
		HashBiMap<Item, Item> fieldParser = HashBiMap.create();
		ArrayList<ItemStack> baseIngredients = new ArrayList<>();
		ArrayList<ArrayList<ItemStack>> fields = new ArrayList<>();
		ArrayList<ArrayList<Item>> codeLines = new ArrayList<>();
		ArrayDeque<Item> identifiers = new ArrayDeque<>();
		identifiers.addAll(SpellStack.identifiers);

		for (Module head : moduleHeads) {
			for (Module module : SpellStack.getAllModules(head)) {
				if (module instanceof IModifier) continue; // TODO
				baseIngredients.add(module.getItemStack().copy());
			}
		}

		for (ItemStack base : baseIngredients) {
			ArrayList<ItemStack> field = new ArrayList<>();
			if (identifiers.peekFirst() == null) break;
			field.add(new ItemStack(identifiers.peek()));
			field.add(base); // TODO modifiers
			field.add(new ItemStack(SpellStack.fieldLineBreak));

			fields.add(field);
			fieldParser.put(identifiers.poll(), base.getItem());
		}

		for (Module head : moduleHeads) {
			ArrayList<Item> codeLine = new ArrayList<>();
			Module tempModule = head;
			while (tempModule != null) {
				Item identifier = fieldParser.inverse().get(tempModule.getItemStack().getItem());
				codeLine.add(identifier);
				tempModule = tempModule.nextModule;
			}
			codeLine.add(SpellStack.codeLineBreak);
			codeLines.add(codeLine);
		}

		for (ArrayList<ItemStack> field : fields) finalList.addAll(field);
		finalList.add(new ItemStack(SpellStack.fieldCodeSplitter));
		for (ArrayList<Item> codeLine : codeLines) {
			for (Item item : codeLine) {
				finalList.add(new ItemStack(item));
			}
		}
		finalList.add(new ItemStack(ModItems.PEARL_NACRE));
	}

	public JsonObject getRecipeJson() {
		if (finalList.isEmpty()) return null;
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();

		for (int i = 0; i < finalList.size() - 1; i++) {
			ItemStack stack = finalList.get(i);
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
