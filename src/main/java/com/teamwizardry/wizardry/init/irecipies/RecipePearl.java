package com.teamwizardry.wizardry.init.irecipies;

import com.teamwizardry.wizardry.api.item.IInfusable;
import com.teamwizardry.wizardry.common.item.ItemNacrePearl;
import com.teamwizardry.wizardry.common.item.ItemRing;
import com.teamwizardry.wizardry.common.item.ItemStaff;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 6/13/2016.
 */
public class RecipePearl extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		boolean foundBaseItem = false;
		boolean foundPearl = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() instanceof ItemRing
					|| stack.getItem() instanceof ItemStaff) {

				if (stack.getItemDamage() == 0)
					foundBaseItem = true;
			}
			if (stack.getItem() instanceof ItemNacrePearl)
				foundPearl = true;

		}
		return foundBaseItem && foundPearl;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack pearl = ItemStack.EMPTY;
		ItemStack baseItem = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() instanceof ItemRing
					|| stack.getItem() instanceof ItemStaff) {
				if (stack.getItemDamage() == 0)
					baseItem = stack;
			}
			if (stack.getItem() instanceof IInfusable)
				pearl = stack;
		}

		ItemStack baseItemCopy = baseItem.copy();
		baseItemCopy.setItemDamage(1);
		if (pearl.hasTagCompound()) baseItemCopy.setTagCompound(pearl.getTagCompound());

		return baseItemCopy;
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
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}

