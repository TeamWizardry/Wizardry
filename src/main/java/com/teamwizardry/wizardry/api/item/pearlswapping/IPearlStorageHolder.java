package com.teamwizardry.wizardry.api.item.pearlswapping;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Implement this for any item with functionality similar to the Pearl Belt.
 */
public interface IPearlStorageHolder {

	@Nullable
	IItemHandler getPearls(ItemStack stack);

	default int getPearlCount(ItemStack holder) {
		IItemHandler handler = getPearls(holder);
		if (handler == null) return 0;

		int total = 0;
		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack pearl = handler.getStackInSlot(i);
			if (pearl.isEmpty()) continue;

			total++;
		}

		return total;
	}

	/**
	 * @return If true, adding the pearl was successful
	 */
	default ItemStack removePearl(ItemStack holder, int slot) {
		if (isDisabled(holder)) return ItemStack.EMPTY;

		IItemHandler handler = getPearls(holder);
		if (handler == null) return ItemStack.EMPTY;

		ItemStack output = handler.extractItem(slot, 1, false);

		sortInv(handler);

		return output;
	}

	default void sortInv(IItemHandler handler) {
		if (handler == null) return;

		Deque<ItemStack> stacks = new ArrayDeque<>();

		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack stack = handler.extractItem(i, 1, false);
			if (stack.isEmpty()) continue;
			stacks.add(stack);
		}

		for (int i = 0; i < handler.getSlots(); i++) {
			if (stacks.isEmpty()) break;
			handler.insertItem(i, stacks.pop(), false);
		}
	}

	/**
	 * @return If true, adding the pearl was successful
	 */
	default boolean addPearl(ItemStack holder, ItemStack pearl) {
		if (isDisabled(holder)) return false;

		IItemHandler handler = getPearls(holder);
		if (handler == null) return false;

		if (getPearlCount(holder) > 6) return false;

		ItemHandlerHelper.insertItem(handler, pearl, false);
		sortInv(handler);

		return true;
	}

	/**
	 * Return true when this belt is disabled for some reason.
	 */
	default boolean isDisabled(ItemStack stack) {
		return false;
	}
}
