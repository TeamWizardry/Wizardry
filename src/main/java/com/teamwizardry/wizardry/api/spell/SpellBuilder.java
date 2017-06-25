package com.teamwizardry.wizardry.api.spell;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.*;

public class SpellBuilder {

	private final static Item fieldLineBreak = Items.WHEAT_SEEDS;
	private final static Item fieldCodeSplitter = ModItems.FAIRY_DUST;
	private final static Item codeLineBreak = ModItems.DEVIL_DUST;
	private final static ArrayList<Item> identifiers = new ArrayList<>();

	// TODO: no...
	static {
		identifiers.add(Items.GOLD_NUGGET);
		identifiers.add(Items.ROTTEN_FLESH);
		identifiers.add(Items.SPECKLED_MELON);
		identifiers.add(ModItems.FAIRY_WINGS);
		identifiers.add(Items.BEETROOT);
		identifiers.add(Items.SPIDER_EYE);
		identifiers.add(Items.PUMPKIN_SEEDS);
		identifiers.add(Items.GHAST_TEAR);
		identifiers.add(ModItems.FAIRY_IMBUED_APPLE);
		identifiers.add(Items.STRING);
		identifiers.add(Items.BONE);
	}

	private ArrayList<ItemStack> inventory;
	private ArrayList<Module> spell;

	public SpellBuilder(ArrayList<ItemStack> inventory) {
		this.inventory = inventory;
		spell = toSpell(inventory);
	}

	public SpellBuilder(HashMap<Integer, ArrayList<Module>> moduleHeads) {
		inventory = new ArrayList<>();

		ArrayDeque<Item> identifiers = new ArrayDeque<>(SpellBuilder.identifiers);
		HashBiMap<Module, ItemStack> baseIngredients = HashBiMap.create();
		HashMultimap<Module, ModuleModifier> modifiers = HashMultimap.create();

		Module lastModule = null;
		for (int i : moduleHeads.keySet())
			for (Module module : SpellUtils.getAllModules(moduleHeads.get(i))) {
				if (module instanceof ModuleModifier) {
					if (lastModule == null) continue;
					modifiers.put(lastModule, (ModuleModifier) module);
				} else {
					if (identifiers.isEmpty()) break;
					lastModule = module;
					baseIngredients.put(module, new ItemStack(identifiers.poll()));
				}
			}

		for (Module module : baseIngredients.keySet()) {
			inventory.add(baseIngredients.get(module));
			inventory.add(module.getItemStack());
			if (modifiers.containsKey(module)) {
				for (ModuleModifier moduleModifier : modifiers.get(module)) {
					ItemStack lastStack = inventory.get(inventory.size() - 1);
					if (ItemStack.areItemsEqual(lastStack, moduleModifier.getItemStack())) {
						lastStack.setCount(lastStack.getCount() + moduleModifier.getItemStack().getCount());
					} else inventory.add(moduleModifier.getItemStack().copy());
				}
			}
			inventory.add(new ItemStack(SpellBuilder.fieldLineBreak));
		}

		inventory.add(new ItemStack(SpellBuilder.fieldCodeSplitter));

		for (int i : moduleHeads.keySet()) {
			for (Module module2 : SpellUtils.getAllModules(moduleHeads.get(i))) {
				if (module2 instanceof ModuleModifier) continue;
				ItemStack stack = baseIngredients.get(module2);
				if (stack == null || stack.isEmpty()) continue;
				inventory.add(stack.copy());
			}
			inventory.add(new ItemStack(SpellBuilder.codeLineBreak));
		}
		inventory.add(new ItemStack(ModItems.PEARL_NACRE));

		spell = toSpell(inventory);
	}

	@Nonnull
	private static List<List<ItemStack>> brancher(List<ItemStack> inventory, Item identifier) {
		List<List<ItemStack>> branches = new ArrayList<>();
		List<ItemStack> temp = new ArrayList<>();
		for (ItemStack stack : inventory) {
			if (ItemStack.areItemsEqual(new ItemStack(identifier), stack)) {
				if (!temp.isEmpty()) branches.add(temp);
				temp = new ArrayList<>();
			} else temp.add(stack);
		}
		if (!temp.isEmpty()) branches.add(temp);
		return branches;
	}

	private ArrayList<Module> toSpell(ArrayList<ItemStack> inventory) {
		HashMap<ItemStack, Module> fields = new HashMap<>();
		ArrayList<Module> compiled = new ArrayList<>();

		ArrayDeque<Item> identifiers = new ArrayDeque<>(SpellBuilder.identifiers);

		List<List<ItemStack>> branches = brancher(inventory, fieldCodeSplitter);
		if (branches.size() != 2) return compiled; // If no fairy dust was found to split the spell twice, stop.

		// PROCESS FIELDS
		List<List<ItemStack>> fieldLines = brancher(branches.get(0), fieldLineBreak); // Get all the fields before the fairy dust.
		if (fieldLines.isEmpty()) return compiled;

		primary:
		for (List<ItemStack> fieldLine : fieldLines) {
			Deque<ItemStack> queue = new ArrayDeque<>(fieldLine);

			ItemStack stack = queue.pollFirst(); // Get the head module of the field. PollFirst removes it from the list too.
			if (!(identifiers.contains(stack.getItem())))
				continue; // If the field doesn't start with an identifier, skip.

			Module head = ModuleRegistry.INSTANCE.getModule(queue.pollFirst()); // If the field's second item is a module, get it.
			if (head == null) continue; // Skip if not.
			if (head instanceof ModuleModifier) continue; // If the field's second item is a modifier, skip it.
			head = head.copy();

			// Everything else gets processed as a modifier to the head.
			if (!queue.isEmpty()) {
				while (!queue.isEmpty()) {
					ItemStack modifierStack = queue.pollFirst();
					if (modifierStack == null || modifierStack.isEmpty()) continue primary;
					Module modifier = ModuleRegistry.INSTANCE.getModule(modifierStack);
					if (modifier == null || !(modifier instanceof ModuleModifier)) continue primary;

					for (int i = 1; i < modifierStack.getCount(); i++) {
						((ModuleModifier) modifier).apply(head);
					}
				}

				head.processModifiers();
			}

			fields.put(stack.copy(), head);
		}

		List<List<ItemStack>> lines = brancher(branches.get(1), codeLineBreak); // Get all the code lines of the second half of the spell.

		// Convert the item code lines to module lines.
		ArrayList<ArrayList<Module>> convertedLines = new ArrayList<>();
		for (List<ItemStack> line : lines) {

			ArrayList<Module> lineModules = new ArrayList<>();
			for (ItemStack stack : line) {
				Module module = null;
				for (ItemStack identifier : fields.keySet())
					if (ItemStack.areItemsEqual(identifier, stack)) {
						module = fields.get(identifier);
						break;
					}
				if (module == null) continue;
				lineModules.add(module.copy());
			}

			convertedLines.add(lineModules);
		}

		// We now have a code line of modules. link them as children in order.
		for (ArrayList<Module> modules : convertedLines) {
			Deque<Module> deque = new ArrayDeque<>();
			deque.addAll(modules);

			for (@SuppressWarnings("unused") Module ignored : modules) {
				if (deque.peekFirst() == deque.peekLast()) {
					compiled.add(deque.peekLast());
					break;
				}
				if (deque.peekLast() != null) {
					Module last = deque.pollLast();
					if (deque.peekLast() != null) {
						Module beforeLast = deque.peekLast();
						beforeLast.nextModule = last;
					}
				}
			}
		}

		// PROCESS COLOR
		for (Module module : compiled) Module.processColor(module);
		return compiled;
	}

	public JsonObject toJson() {
		if (inventory.isEmpty()) return null;
		JsonObject object = new JsonObject();
		JsonArray array = new JsonArray();

		for (ItemStack stack : inventory) {
			ResourceLocation location = stack.getItem().getRegistryName();
			if (location == null) continue;

			JsonObject obj = new JsonObject();
			obj.addProperty("name", location.toString());
			obj.addProperty("meta", stack.getItemDamage());
			obj.addProperty("count", stack.getCount());
			array.add(obj);
		}

		object.add("list", array);

		StringBuilder finalName = null;
		ArrayList<ArrayList<Module>> modules = SpellUtils.getModules(spell);
		Module lastModule = null;
		for (ArrayList<Module> module0 : modules)
			for (Module module : SpellUtils.getAllModules(module0)) {
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

	public ArrayList<ItemStack> getInventory() {
		return inventory;
	}

	public ArrayList<Module> getSpell() {
		return spell;
	}
}
