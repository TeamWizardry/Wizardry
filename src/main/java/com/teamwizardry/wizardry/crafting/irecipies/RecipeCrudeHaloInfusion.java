package com.teamwizardry.wizardry.crafting.irecipies;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.item.halo.HaloInfusionItem;
import com.teamwizardry.wizardry.api.item.halo.HaloInfusionItemRegistry;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by Demoniaque on 8/30/2016.
 */
public class RecipeCrudeHaloInfusion extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		ItemStack foundHalo = ItemStack.EMPTY;
		boolean foundGlueStick = false;

		int availableItems = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == Items.SLIME_BALL) {
				if (foundGlueStick) return false;
				foundGlueStick = true;
			} else if (stack.getItem() == ModItems.FAKE_HALO) {
				if (!foundHalo.isEmpty()) return false;
				foundHalo = stack;
			} else if (HaloInfusionItemRegistry.isHaloInfusionItem(stack))
				availableItems++;
			else if (!stack.isEmpty()) {
				return false;
			}
		}

		if (!foundGlueStick || foundHalo.isEmpty() || availableItems <= 0) return false;

		NBTTagList slots = ItemNBTHelper.getList(foundHalo, "slots", NBTTagString.class);
		if (slots == null) return availableItems <= 7;

		int freeSlots = 0;
		for (int j = 0; j < slots.tagCount(); j++) {
			if (freeSlots >= 7) break;
			String string = slots.getStringTagAt(j);
			HaloInfusionItem infusionItem = HaloInfusionItemRegistry.getItemFromName(string);
			if (infusionItem == HaloInfusionItemRegistry.EMPTY) freeSlots++;
		}

		return freeSlots >= availableItems;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack foundHalo = ItemStack.EMPTY;
		ItemStack foundGlueStick = ItemStack.EMPTY;
		Deque<HaloInfusionItem> infusionItems = new ArrayDeque<>();

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == Items.SLIME_BALL) foundGlueStick = stack;
			else if (stack.getItem() == ModItems.FAKE_HALO) foundHalo = stack;
			else if (HaloInfusionItemRegistry.isHaloInfusionItem(stack))
				infusionItems.add(HaloInfusionItemRegistry.getInfusionItemFromStack(stack));
		}

		if (foundHalo.isEmpty() || foundGlueStick.isEmpty() || infusionItems.isEmpty()) return ItemStack.EMPTY;

		NBTTagList slots = ItemNBTHelper.getList(foundHalo, "slots", NBTTagString.class);
		if (slots == null) {
			slots = new NBTTagList();

			for (int i = 0; i < 7; i++) {
				slots.appendTag(new NBTTagString(HaloInfusionItemRegistry.EMPTY.getNbtName()));
			}

		} else slots = slots.copy();

		final int count = slots.tagCount();
		for (int i = 0; i < count; i++) {
			if (infusionItems.isEmpty()) break;

			String string = slots.getStringTagAt(i);
			HaloInfusionItem infusionItem = HaloInfusionItemRegistry.getItemFromName(string);

			if (infusionItem == HaloInfusionItemRegistry.EMPTY) {
				slots.set(i, new NBTTagString(infusionItems.pop().getNbtName()));
			}
		}

		ItemStack haloCopy = foundHalo.copy();

		ItemNBTHelper.setList(haloCopy, "slots", slots);

		return haloCopy;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
		NonNullList<ItemStack> remainingItems = ForgeHooks.defaultRecipeGetRemainingItems(inv);

		ItemStack gluestick;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == Items.SLIME_BALL) {
				gluestick = stack.copy();
				remainingItems.set(i, gluestick);
				break;
			}
		}

		return remainingItems;
	}
}
