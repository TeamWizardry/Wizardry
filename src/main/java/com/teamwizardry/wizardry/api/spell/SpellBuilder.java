package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

public class SpellBuilder {

	private List<ItemStack> inventory;
	private List<SpellRing> spell;
	
	public SpellBuilder(List<ItemStack> inventory)
	{
		this.inventory = inventory;
		spell = toSpell(inventory, 1);
	}
	
	public SpellBuilder(List<ItemStack> inventory, double pearlMultiplier) {
		this.inventory = inventory;
		spell = toSpell(inventory, pearlMultiplier);
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

	private List<SpellRing> toSpell(List<ItemStack> inventory, double pearlMultiplier) {
		List<SpellRing> spellList = new ArrayList<>();
		Set<List<SpellRing>> spellChains = new HashSet<>();

		List<List<ItemStack>> lines = brancher(inventory, SpellUtils.CODE_LINE_BREAK);

		// Spell chain from multiple chains
		for (List<ItemStack> line : lines) {

			// List is made of all modules that aren't modifiers for this spellData chain.
			Deque<SpellRing> uncompressedChain = new ArrayDeque<>();

			// Step through each item in line. If modifier, add to lastModule, if not, add to compiled.
			for (ItemStack stack : line) {
				Module module = ModuleRegistry.INSTANCE.getModule(stack);

				if (module == null) continue;

				if (module instanceof ModuleModifier) {
					if (!uncompressedChain.isEmpty()) {
						for (int i = 0; i < stack.getCount(); i++) {
							SpellRing lastRing = uncompressedChain.peekLast();
							lastRing.addModifier((ModuleModifier) module);
						}
					}
				} else {
					for (int i = 0; i < stack.getCount(); i++) {
						SpellRing ring = new SpellRing(module);
						uncompressedChain.add(ring);
					}
				}
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
			List<ModuleModifier> cascadingModifiers = new LinkedList<>();
			while (chainEnd != null) {
				for (ModuleModifier modifier : cascadingModifiers)
					chainEnd.addModifier(modifier);
				if (chainEnd.getChildRing() == null) {
					if (chainEnd.getModule() != null) {
						chainEnd.setPrimaryColor(chainEnd.getModule().getPrimaryColor());
						chainEnd.setSecondaryColor(chainEnd.getModule().getSecondaryColor());
					}
					chainEnd.updateColorChain();
				}
				chainEnd.processModifiers();
				chainEnd = chainEnd.getChildRing();
			}
		}
		return spellList;
	}

	public List<ItemStack> getInventory() {
		return inventory;
	}

	public List<SpellRing> getSpell() {
		return spell;
	}
}
