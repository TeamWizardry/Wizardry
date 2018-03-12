package com.teamwizardry.wizardry.api.spell;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
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
	private List<SpellRing> spell;

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

	private List<SpellRing> toSpell(List<ItemStack> inventory) {
		List<SpellRing> spellList = new ArrayList<>();
		Set<List<SpellRing>> compiled = new HashSet<>();

		List<List<ItemStack>> lines = brancher(inventory, codeLineBreak);

		SpellRing lastModule = null;

		// Spell chain from multiple chains
		for (List<ItemStack> line : lines) {

			// List is made of all modules that aren't modifiers for this spellData chain.
			List<SpellRing> lineModule = new ArrayList<>();

			// Each module get's it's list of modifiers.
			HashMap<SpellRing, List<AttributeModifier>> modifiersToApply = new HashMap<>();

			// Step through each item in line. If modifier, add to lastModule, if not, add to compiled.
			for (ItemStack stack : line) {
				Module module = ModuleRegistry.INSTANCE.getModule(stack);

				if (module == null) continue;

				SpellRing ring = new SpellRing(module);

				if (module instanceof ModuleModifier) {
					if (lastModule == null) continue;

					modifiersToApply.putIfAbsent(lastModule, new ArrayList<>());
					List<AttributeModifier> modifiers = modifiersToApply.get(lastModule);

					for (int i = 0; i < stack.getCount(); i++)
						((ModuleModifier) module).apply(modifiers);
				} else {
					lastModule = ring;
					lineModule.add(ring);
				}
			}

			// Process all module modifiers.
			for (SpellRing ring : modifiersToApply.keySet()) {
				ring.processModifiers(modifiersToApply.get(ring));
			}
			compiled.add(lineModule);
		}

		// We now have a code line of modules. link them as children in order.
		for (List<SpellRing> rings : compiled) {
			Deque<SpellRing> deque = new ArrayDeque<>(rings);

			for (@SuppressWarnings("unused") SpellRing ignored : rings) {
				if (deque.peekFirst() == deque.peekLast()) {
					spellList.add(deque.peekLast());
					break;
				}
				if (deque.peekLast() != null) {
					SpellRing last = deque.pollLast();
					if (deque.peekLast() != null) {
						SpellRing beforeLast = deque.peekLast();
						beforeLast.setChildRing(last);
						last.setParentRing(beforeLast);
					}
				}
			}
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

		return object;
	}

	public List<ItemStack> getInventory() {
		return inventory;
	}

	public List<SpellRing> buildSpell() {
		return spell;
	}
}
