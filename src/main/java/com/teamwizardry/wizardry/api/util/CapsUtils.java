package com.teamwizardry.wizardry.api.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class CapsUtils {

	public static int getOccupiedSlotCount(ItemStackHandler inventory) {
		int x = 0;
		for (int i = 0; i < inventory.getSlots(); i++) if (inventory.getStackInSlot(i) != null) x++;
		return x;
	}

	public static int getLastOccupiedSlot(ItemStackHandler inventory) {
		for (int i = inventory.getSlots() - 1; i > 0; i--) if (inventory.getStackInSlot(i) != null) return i;
		return 0;
	}

	public static List<ItemStack> getListOfItems(ItemStackHandler inventory) {
		List<ItemStack> stacks = new ArrayList<>();
		for (int i = 0; i < inventory.getSlots(); i++)
			if (inventory.getStackInSlot(i) != null) stacks.add(inventory.getStackInSlot(i));
		return stacks;
	}

	public static void clearInventory(ItemStackHandler inventory) {
		for (int i = 0; i < inventory.getSlots(); i++)
			inventory.setStackInSlot(i, null);
	}
}
