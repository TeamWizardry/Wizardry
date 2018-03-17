package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.spell.attribute.AttributeModifier;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

public class SpellBuilder {

	// TODO: config this
	private final static Item codeLineBreak = ModItems.DEVIL_DUST;

	private List<ItemStack> inventory;
	private List<SpellRing> spell;

	public SpellBuilder(List<ItemStack> inventory, boolean javaIsStupid) {
		this.inventory = inventory;
		spell = toSpell(inventory);
	}

	public SpellBuilder(List<SpellRing> chains, boolean javaisReallyStupid, boolean fml) {
		this.spell = chains;

		Deque<ItemStack> dequeItems = new ArrayDeque<>();

		for (SpellRing chainHeads : chains) {

			for (SpellRing spellRing : SpellUtils.getAllSpellRings(chainHeads)) {
				if (spellRing.getModule() == null) continue;

				dequeItems.add(spellRing.getModule().getItemStack().copy());

				for (AttributeModifier modifier : spellRing.getAttributes()) {
					if (modifier.getModuleSet().isEmpty()) continue;
					for (Module module : modifier.getModuleSet()) {
						ItemStack stack = module.getItemStack().copy();
						//stack.setCount((int) modifier.getModifier());
						dequeItems.add(stack);
					}
				}
			}

			dequeItems.add(new ItemStack(codeLineBreak));
		}
		dequeItems.add(new ItemStack(ModItems.PEARL_NACRE));

		inventory = new ArrayList<>(dequeItems);
	}

	public SpellBuilder(List<List<Module>> modules) {
		Deque<ItemStack> dequeItems = new ArrayDeque<>();

		for (List<Module> moduleChain : modules) {
			for (Module module : moduleChain) {

				if (module instanceof ModuleModifier) {

					ItemStack lastStack = dequeItems.peekLast();
					if (lastStack.isItemEqual(module.getItemStack())) {
						ItemStack stack = dequeItems.pollLast();
						stack.setCount(stack.getCount() + module.getItemStack().getCount());
						dequeItems.add(stack);
					} else dequeItems.add(module.getItemStack().copy());

				} else {
					dequeItems.add(module.getItemStack().copy());
				}
			}
		}
		dequeItems.add(new ItemStack(ModItems.PEARL_NACRE));

		inventory = new ArrayList<>(dequeItems);

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
		Set<List<SpellRing>> spellChains = new HashSet<>();

		List<List<ItemStack>> lines = brancher(inventory, codeLineBreak);

		// Spell chain from multiple chains
		for (List<ItemStack> line : lines) {

			// List is made of all modules that aren't modifiers for this spellData chain.
			Deque<SpellRing> uncompressedChain = new ArrayDeque<>();

			List<AttributeModifier> modifiersToApply = new ArrayList<>();

			// Step through each item in line. If modifier, add to lastModule, if not, add to compiled.
			for (ItemStack stack : line) {
				Module module = ModuleRegistry.INSTANCE.getModule(stack);

				if (module == null) continue;

				if (module instanceof ModuleModifier) {

					for (int i = 0; i < stack.getCount(); i++)
						((ModuleModifier) module).apply(modifiersToApply);

				} else {
					if (!modifiersToApply.isEmpty() && !uncompressedChain.isEmpty()) {
						SpellRing lastRing = uncompressedChain.peekLast();
						lastRing.processModifiers(modifiersToApply);
						modifiersToApply.clear();
					}

					SpellRing ring = new SpellRing(module);
					uncompressedChain.add(ring);
				}
			}

			if (!modifiersToApply.isEmpty()) {
				SpellRing lastRing = uncompressedChain.peekLast();
				lastRing.processModifiers(modifiersToApply);
				modifiersToApply.clear();
			}

			spellChains.add(new ArrayList<>(uncompressedChain));
		}

		// We now have a code line of modules. link them as children in order.

		for (List<SpellRing> rings : spellChains) {
			if (rings.isEmpty()) continue;

			Deque<SpellRing> deque = new ArrayDeque<>(rings);

			SpellRing ringHead = deque.pop();

			SpellRing lastRing = ringHead;
			while (!deque.isEmpty()) {
				SpellRing child = deque.pop();
				lastRing.setChildRing(child);
				child.setParentRing(lastRing);
				lastRing = child;
			}

			spellList.add(ringHead);

		}

		for (SpellRing ring : spellList) {
			SpellRing chainEnd = ring;
			while (chainEnd != null) {
				if (chainEnd.getChildRing() == null) break;
				chainEnd = chainEnd.getChildRing();
			}

			if (chainEnd != null) {
				chainEnd.updateColorChain();
			}
		}
		return spellList;
	}

	public List<ItemStack> getInventory() {
		return inventory;
	}

	public List<SpellRing> buildSpell() {
		return spell;
	}
}
