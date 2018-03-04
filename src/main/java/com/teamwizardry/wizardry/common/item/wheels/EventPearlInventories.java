package com.teamwizardry.wizardry.common.item.wheels;

import com.google.common.collect.Lists;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.IItemHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author WireSegal
 * Created at 12:25 PM on 3/4/18.
 */
@Cancelable
public class EventPearlInventories extends Event {
	private final TObjectIntHashMap<Iterable<ItemStack>> inventories = new TObjectIntHashMap<>();

	public final EntityPlayer player;

	public EventPearlInventories(EntityPlayer player) {
		this.player = player;
	}

	public void addItems(IItemHandler handler, int priority) {
		addItems(() -> new CapabilityIterator(handler), priority);
	}

	public void addItems(IInventory handler, int priority) {
		addItems(() -> new InventoryIterator(handler), priority);
	}

	public void addItems(ItemStack handler, int priority) {
		addItems(Lists.newArrayList(handler), priority);
	}

	public void addItems(Iterable<ItemStack> handler, int priority) {
		inventories.put(handler, priority);
	}

	public Iterator<ItemStack> getCombinedIterator() {
		if (isCanceled())
			return Collections.emptyIterator();

		List<Iterable<ItemStack>> inventoriesSorted = Lists.newArrayList(inventories.keySet());
		inventoriesSorted.sort(Comparator.comparingInt(inventories::get).reversed());

		return new CombinedIterator<>(inventoriesSorted);
	}


	private static final class InventoryIterator implements Iterator<ItemStack> {
		private final IInventory inventory;
		private int slot = 0;

		public InventoryIterator(IInventory inventory) {
			this.inventory = inventory;
		}

		@Override
		public boolean hasNext() {
			return inventory.getSizeInventory() >= slot;
		}

		@Override
		public ItemStack next() {
			return inventory.getStackInSlot(slot++);
		}
	}

	private static final class CapabilityIterator implements Iterator<ItemStack> {
		private final IItemHandler inventory;
		private int slot = 0;

		public CapabilityIterator(IItemHandler inventory) {
			this.inventory = inventory;
		}

		@Override
		public boolean hasNext() {
			return inventory.getSlots() >= slot;
		}

		@Override
		public ItemStack next() {
			return inventory.getStackInSlot(slot++);
		}
	}

	private static final class CombinedIterator<T> implements Iterator<T> {
		private final Iterator<Iterable<T>> iterators;
		private Iterator<T> current;

		private final Iterator<Iterable<T>> stepAhead;
		private boolean atParity = true;
		private Iterator<T> checkAgainst;

		public CombinedIterator(Iterable<Iterable<T>> iterators) {
			this.iterators = iterators.iterator();
			this.stepAhead = iterators.iterator();

			if (this.iterators.hasNext()) {
				this.current = this.iterators.next().iterator();
				this.checkAgainst = stepAhead.next().iterator();
			}

		}

		@Override
		public boolean hasNext() {
			if (current.hasNext()) return true;
			else if (iterators.hasNext()) {
				if (atParity) {
					checkAgainst = stepAhead.next().iterator();
					atParity = false;
				}
				return checkAgainst.hasNext();
			} else
				return false;
		}

		@Override
		public T next() {
			if (current.hasNext())
				return current.next();
			current = iterators.next().iterator();
			atParity = true;
			return current.next();
		}
	}
}
