package com.teamwizardry.wizardry.api.item.pearlswapping;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * @author WireSegal
 * Created at 12:23 PM on 3/4/18.
 */
public interface IPearlWheelHolder {

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
		if (!shouldUse(holder)) return ItemStack.EMPTY;

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
		if (!shouldUse(holder)) return false;

		IItemHandler handler = getPearls(holder);
		if (handler == null) return false;

		if (getPearlCount(holder) > 6) return false;

		ItemHandlerHelper.insertItem(handler, pearl, false);
		sortInv(handler);

		return true;
	}

	/**
	 * Return false when this belt is disabled for some reason.
	 */
	default boolean shouldUse(ItemStack stack) {
		return true;
	}

	static ItemStack getPearlHolder(EntityPlayer player) {
		FindPearlWheelEvent event = new FindPearlWheelEvent(player);

		event.addItems(player.inventory.mainInventory, 0);
		event.addItems(player.inventory.armorInventory, 1);
		event.addItems(player.inventory.offHandInventory, 10000);

		MinecraftForge.EVENT_BUS.post(event);

		Iterator<ItemStack> stacks = event.getCombinedIterator();
		while (stacks.hasNext()) {
			ItemStack next = stacks.next();
			if (next.getItem() instanceof IPearlWheelHolder && ((IPearlWheelHolder) next.getItem()).shouldUse(next))
				return next;
		}
		return ItemStack.EMPTY;
	}
}
