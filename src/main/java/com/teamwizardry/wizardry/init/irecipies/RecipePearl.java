package com.teamwizardry.wizardry.init.irecipies;

import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.common.item.ItemRing;
import com.teamwizardry.wizardry.common.item.ItemStaff;
import com.teamwizardry.wizardry.common.item.pearl.ItemNacrePearl;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Created by Saad on 6/13/2016.
 */
public class RecipePearl implements IRecipe {
	@Override
	public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
		boolean foundBaseItem = false;
		boolean foundPearl = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (stack.getItem() instanceof ItemRing
						|| stack.getItem() instanceof ItemStaff) {

					if (stack.getItemDamage() == 0)
						foundBaseItem = true;
				}
				if (stack.getItem() instanceof ItemNacrePearl)
					foundPearl = true;
			}
		}
		return foundBaseItem && foundPearl;
	}

	@Nullable
	@Override
	public ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
		ItemStack pearl = null;
		ItemStack baseItem = null;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (stack.getItem() instanceof ItemRing
						|| stack.getItem() instanceof ItemStaff) {
					if (stack.getItemDamage() == 0)
						baseItem = stack;
				}
				if (stack.getItem() instanceof Infusable)
					pearl = stack;
			}
		}

		if (pearl == null || baseItem == null)
			return null;

		ItemStack baseItemCopy = baseItem.copy();
		baseItemCopy.setItemDamage(1);
		if (pearl.hasTagCompound()) baseItemCopy.setTagCompound(pearl.getTagCompound());

		return baseItemCopy;
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Nullable
	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@NotNull
	@Override
	public ItemStack[] getRemainingItems(@NotNull InventoryCrafting inv) {
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}

