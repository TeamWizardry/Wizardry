package com.teamwizardry.wizardry.api.capability.item;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author WireSegal
 * Created at 4:27 PM on 12/15/18.
 * Taken from: https://github.com/Kamefrede/rpsideas/blob/master/src/main/java/com/kamefrede/rpsideas/items/base/ProxiedItemStackHandler.java
 */
public class ProxiedItemStackHandler implements IItemHandler, IItemHandlerModifiable, ICapabilityProvider {
	protected final ItemStack stack;
	protected final String key;
	protected final int size;

	public ProxiedItemStackHandler(ItemStack stack) {
		this(stack, "Inventory", 1);
	}

	public ProxiedItemStackHandler(ItemStack stack, String key) {
		this(stack, key, 1);
	}

	public ProxiedItemStackHandler(ItemStack stack, int size) {
		this(stack, "Inventory", size);
	}

	public ProxiedItemStackHandler(ItemStack stack, String key, int size) {
		this.stack = stack;
		this.key = key;
		this.size = size;
	}

	private NBTTagList getStackList() {
		NBTTagList list = ItemNBTHelper.getList(stack, key, Constants.NBT.TAG_COMPOUND);
		if (list == null)
			ItemNBTHelper.setList(stack, key, list = new NBTTagList());

		while (list.tagCount() < size)
			list.appendTag(new NBTTagCompound());

		return list;
	}

	private void writeStack(int index, @Nonnull ItemStack stack) {
		stack.writeToNBT(getStackList().getCompoundTagAt(index));
		onContentsChanged(index);
	}

	private ItemStack readStack(int index) {
		return new ItemStack(getStackList().getCompoundTagAt(index));
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		validateSlotIndex(slot);
		writeStack(slot, stack);
		onContentsChanged(slot);
	}

	@Override
	public int getSlots() {
		return size;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		validateSlotIndex(slot);
		return readStack(slot);
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		if (stack.isEmpty())
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		ItemStack existing = readStack(slot);

		int limit = getStackLimit(slot, stack);

		if (!existing.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.getCount();
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.getCount() > limit;

		if (!simulate)
			writeStack(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (amount == 0)
			return ItemStack.EMPTY;

		validateSlotIndex(slot);

		ItemStack existing = readStack(slot);

		if (existing.isEmpty())
			return ItemStack.EMPTY;

		int toExtract = Math.min(amount, existing.getMaxStackSize());

		if (existing.getCount() <= toExtract) {
			if (!simulate)
				writeStack(slot, ItemStack.EMPTY);

			return existing;
		} else {
			if (!simulate)
				writeStack(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));

			return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
		}
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return true;
	}

	protected void validateSlotIndex(int slot) {
		if (slot < 0 || slot >= size)
			throw new RuntimeException("Slot " + slot + " not in valid range - [0," + size + ")");
	}

	@SuppressWarnings("unused")
	protected void onContentsChanged(int slot) {
		// NO-OP
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ?
				CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this) : null;
	}
}