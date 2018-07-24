package com.teamwizardry.wizardry.crafting.irecipies;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.item.halo.HaloInfusionItem;
import com.teamwizardry.wizardry.api.item.halo.HaloInfusionItemRegistry;
import com.teamwizardry.wizardry.init.ModItems;
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
			if (stack.getItem() == ModItems.GLUE_STICK) foundGlueStick = true;
			else if (stack.getItem() == ModItems.FAKE_HALO)
				foundHalo = stack;
			else if (HaloInfusionItemRegistry.isHaloInfusionItem(stack))
				availableItems++;
			else return false;
		}

		if (!foundGlueStick || foundHalo.isEmpty() || availableItems <= 0) return false;

		NBTTagList slots = ItemNBTHelper.getList(foundHalo, "slots", NBTTagString.class);
		if (slots == null) return true;

		int freeSlots = 0;
		for (int j = 0; j < slots.tagCount(); j++) {
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
		ItemStack foundInfusionItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == ModItems.GLUE_STICK) foundGlueStick = stack;
			else if (stack.getItem() == ModItems.FAKE_HALO) foundHalo = stack;
			else if (HaloInfusionItemRegistry.isHaloInfusionItem(stack)) foundInfusionItem = stack;
		}

		if (foundHalo.isEmpty() || foundGlueStick.isEmpty() || foundInfusionItem.isEmpty()) return ItemStack.EMPTY;
		HaloInfusionItem infuse = HaloInfusionItemRegistry.getInfusionItemFromStack(foundInfusionItem);
		if (infuse == HaloInfusionItemRegistry.EMPTY) return ItemStack.EMPTY;

		NBTTagList slots = ItemNBTHelper.getList(foundHalo, "slots", NBTTagString.class);
		if (slots == null || slots.tagCount() < HaloInfusionItemRegistry.getItems().size()) {
			slots = new NBTTagList();

			for (int i = 0; i < HaloInfusionItemRegistry.getItems().size(); i++) {
				slots.appendTag(new NBTTagString(HaloInfusionItemRegistry.EMPTY.getNbtName()));
			}
		}

		int slot = 0;
		for (int i = 0; i < slots.tagCount(); i++) {
			slot = i;
			String string = slots.getStringTagAt(i);
			HaloInfusionItem infusionItem = HaloInfusionItemRegistry.getItemFromName(string);
			if (infusionItem == HaloInfusionItemRegistry.EMPTY) break;
		}

		slots.set(slot, new NBTTagString(infuse.getNbtName()));

		ItemStack haloCopy = foundHalo.copy();

		ItemNBTHelper.setList(haloCopy, "slots", slots);

		return haloCopy;
	}

	@Override
	public boolean canFit(int width, int height) {
		return true;
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
			if (stack.getItem() == ModItems.GLUE_STICK) {
				gluestick = stack.copy();
				remainingItems.set(i, gluestick);
				break;
			}
		}

		return remainingItems;
	}
}
