package com.teamwizardry.wizardry.common.spell.parsing;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.IRequireItem;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Parser {

	private final Deque<ItemStack> stacks;
	private int endCount;

	public Parser(List<ItemStack> items) {
		stacks = new ArrayDeque<>(items);
	}

	/**
	 * Will convert the list of items into a Module with all the respective
	 * children.
	 *
	 * @return A module with children that was translated from the inventory
	 * provided
	 */
	public Module parseInventoryToModule() {
		return parseInventoryToModule(ModuleType.EVENT, this.getDefaultType(ModuleType.EVENT));
	}

	@Nullable
	public Module parseInventoryToModule(ModuleType currentType, ModuleType expectedType) {
		Module module = getModuleFromItemStack(stacks.pop(), expectedType);
		if (module == null)
			return null;
		currentType = module.getType();
		expectedType = getDefaultType(currentType);
		if (module.canHaveChildren()) {
			while ((endCount == 0) && (expectedType != null)) {
				if (stacks.isEmpty())
					return module;
				ItemStack peek = stacks.peek();
				endCount = getEndCount(peek) * peek.stackSize;
				if (endCount != 0) {
					while ((endCount != 0) && (expectedType != null)) {
						expectedType = getNextType(currentType, expectedType);
						endCount--;
					}
					if (endCount != 0) {
						stacks.pop();
						break;
					}
				}

				if (expectedType == null)
					return module;
				Module childModule = parseInventoryToModule(currentType, expectedType);
				if (childModule != null)
					module.accept(childModule);
			}
			endCount--;
		}
		return module;
	}

	/**
	 * Gets a new instance of the module given an item
	 */
	private Module getModuleFromItemStack(ItemStack stack, ModuleType type) {
		@Nullable Module module = ModuleRegistry.getInstance().getModuleFromItemStack(stack, type);
		if (module != null) {
			if (stack.stackSize > module.stack.stackSize) {
				stack.stackSize -= module.stack.stackSize;
				stacks.push(stack);
			} else if (stack.stackSize < module.stack.stackSize)
				module = null;
		}
		if (module instanceof IRequireItem)
			((IRequireItem) module).handle(stack);
		return module;
	}

	/**
	 * Gets the number of levels to end given an item. 0 if the item isn't an
	 * end item
	 */
	private int getEndCount(ItemStack stack) {
		if (stack == null)
			return 0;
		return (stack.getItem() == ModItems.DEVIL_DUST) ? 1 : 0;
	}

	private ModuleType getNextType(ModuleType currentType, ModuleType expectedType) {
		switch (currentType) {
			case MODIFIER:
			case EFFECT:
				return null;
			case SHAPE:
				if (expectedType == ModuleType.MODIFIER)
					return ModuleType.BOOLEAN;
			case BOOLEAN:
				if (expectedType == ModuleType.BOOLEAN)
					return ModuleType.EVENT;
				if (expectedType == ModuleType.EVENT)
					return ModuleType.EFFECT;
			case EVENT:
				if (expectedType == ModuleType.EFFECT)
					return ModuleType.SHAPE;
		}
		return null;
	}

	private ModuleType getDefaultType(ModuleType currentType) {
		switch (currentType) {
			case MODIFIER:
				return ModuleType.MODIFIER;
			case EFFECT:
				return ModuleType.MODIFIER;
			case EVENT:
				return ModuleType.SHAPE;
			case SHAPE:
				return ModuleType.MODIFIER;
			case BOOLEAN:
				return ModuleType.BOOLEAN;
		}
		return null;
	}
}
