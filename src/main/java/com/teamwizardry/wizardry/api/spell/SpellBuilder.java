package com.teamwizardry.wizardry.api.spell;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.*;

public class SpellBuilder {

	private final static Item codeLineBreak = ModItems.DEVIL_DUST;

	private List<ItemStack> inventory;
	private List<Module> spell;

	public SpellBuilder(List<ItemStack> inventory) {
		this.inventory = inventory;
		spell = toSpell(inventory);
	}

	public SpellBuilder(HashSet<ArrayList<Module>> moduleHeads) {
		inventory = new ArrayList<>();

		for (ArrayList<Module> modules : moduleHeads) {
			for (Module module : modules) {
				if (module instanceof ModuleModifier) {
					ModuleModifier moduleModifier = (ModuleModifier) module;
					ItemStack lastStack = inventory.get(inventory.size() - 1);
					if (ItemStack.areItemsEqual(lastStack, moduleModifier.getItemStack())) {
						lastStack.setCount(lastStack.getCount() + moduleModifier.getItemStack().getCount());
					} else inventory.add(moduleModifier.getItemStack().copy());
				} else inventory.add(module.getItemStack().copy());
			}
			inventory.add(new ItemStack(codeLineBreak));
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

	private List<Module> toSpell(List<ItemStack> inventory) {
		List<Module> spellList = new ArrayList<>();
		Set<List<Module>> compiled = new HashSet<>();

		List<List<ItemStack>> lines = brancher(inventory, codeLineBreak);

		Module lastModule = null;
		for (List<ItemStack> line : lines) {
			List<Module> lineModule = new ArrayList<>();
			for (ItemStack stack : line) {
				Module module = ModuleRegistry.INSTANCE.getModule(stack);
				if (module == null) continue;
				if (module instanceof ModuleModifier) {
					if (lastModule == null) continue;
					((ModuleModifier) module).apply(lastModule);
				} else {
					lastModule = module;
					lineModule.add(module);
				}
			}
			lineModule.forEach(Module::processModifiers);
			compiled.add(lineModule);
		}

		// We now have a code line of modules. link them as children in order.
		for (List<Module> modules : compiled) {
			Deque<Module> deque = new ArrayDeque<>();
			deque.addAll(modules);

			for (@SuppressWarnings("unused") Module ignored : modules) {
				if (deque.peekFirst() == deque.peekLast()) {
					spellList.add(deque.peekLast());
					break;
				}
				if (deque.peekLast() != null) {
					Module last = deque.pollLast();
					if (deque.peekLast() != null) {
						Module beforeLast = deque.peekLast();
						beforeLast.nextModule = last;
						last.prevModule = beforeLast;
					}
				}
			}
		}

		// PROCESS COLOR
		for (Module module : spellList) {
			module.setIsHead(true);
			Module.processColor(module);
		}
		return spellList;
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

	public List<ItemStack> getInventory() {
		return inventory;
	}

	public List<Module> getSpell() {
		return spell;
	}
}
