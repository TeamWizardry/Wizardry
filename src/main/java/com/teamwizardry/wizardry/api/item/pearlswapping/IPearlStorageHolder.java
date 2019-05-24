package com.teamwizardry.wizardry.api.item.pearlswapping;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
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
	 * @return The popped pearl.
	 */
	default ItemStack removePearl(ItemStack holder, int slot, boolean sort) {
		if (isDisabled(holder)) return ItemStack.EMPTY;

		IItemHandler handler = getPearls(holder);
		if (handler == null) return ItemStack.EMPTY;

		ItemStack output = handler.extractItem(slot, 1, false);

		if (sort)
			sortInv(handler);

		return output;
	}

	default void sortInv(IItemHandler handler) {
		if (handler == null) return;

		Deque<ItemStack> stacks = new ArrayDeque<>();

		final int slots = handler.getSlots();
		for (int i = 0; i < slots; i++) {
			ItemStack stack = handler.extractItem(i, 1, false);
			if (stack.isEmpty()) continue;
			stacks.add(stack);
		}

		for (int i = 0; i < slots; i++) {
			if (stacks.isEmpty()) break;
			handler.insertItem(i, stacks.pop(), false);
		}
	}

	/**
	 * @return If true, adding the pearl was successful
	 */
	default boolean addPearl(ItemStack holder, ItemStack pearl, boolean sort) {
		if (isDisabled(holder)) return false;

		IItemHandler handler = getPearls(holder);
		if (handler == null) return false;

		if (getPearlCount(holder) > ConfigValues.pearlBeltInvSize) return false;

		ItemHandlerHelper.insertItem(handler, pearl, false);

		if (sort)
			sortInv(handler);

		return true;
	}


	/**
	 * Moves all pearls from the player's inv to this storage holder's inventory.
	 * <p>
	 * returns if any pearls were succeed in to indicate that the storage holder has been updated.
	 */
	default boolean succPearls(EntityPlayer player) {
		ItemStack belt = player.getHeldItemMainhand();
		if (isDisabled(belt)) return false;

		boolean changed = false;
		for (ItemStack stack : player.inventory.mainInventory)
			if (stack.getItem() == ModItems.PEARL_NACRE)
				if (NBTHelper.getBoolean(stack, "infused", false)) {
					if (getPearlCount(belt) >= ConfigValues.pearlBeltInvSize) break;

					if (addPearl(belt, stack.copy(), true)) {
						stack.shrink(1);
						changed = true;
					}
				}

		return changed;
	}

	/**
	 * Return true when this belt is disabled for some reason.
	 */
	default boolean isDisabled(ItemStack stack) {
		return false;
	}
}
