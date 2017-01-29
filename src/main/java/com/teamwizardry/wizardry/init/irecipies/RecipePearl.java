package com.teamwizardry.wizardry.init.irecipies;

import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.common.item.ItemRing;
import com.teamwizardry.wizardry.common.item.ItemStaff;
import com.teamwizardry.wizardry.init.ModItems;
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
	public boolean matches(@NotNull InventoryCrafting inv, World worldIn) {
		boolean foundBaseItem = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null && stack.getItem() instanceof ItemStaff) {
				foundBaseItem = true;
			}
		}
		return foundBaseItem;
	}

	@Nullable
	@Override
	public ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
		ItemStack pearl = null;
		ItemStack baseItem = null;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (stack.getItem() instanceof ItemRing || stack.getItem() instanceof ItemStaff)
					baseItem = stack;
				if (stack.getItem() instanceof Infusable)
					pearl = stack;
			}
		}

		if (baseItem == null) return null;

		if (pearl != null) {
			if (baseItem.getItemDamage() == 0) {
				ItemStack baseItemCopy = baseItem.copy();
				baseItemCopy.setItemDamage(1);
				if (pearl.hasTagCompound()) baseItemCopy.setTagCompound(pearl.getTagCompound());
				return baseItemCopy;
			} else {
				ItemStack newPearl = new ItemStack(ModItems.PEARL_NACRE);
				if (pearl.hasTagCompound()) newPearl.setTagCompound(pearl.getTagCompound());
				boolean flag = false;
				for (int i = 0; i < inv.getSizeInventory(); i++)
					if (inv.isItemValidForSlot(i, newPearl)) {
						inv.setInventorySlotContents(i, newPearl);
						flag = true;
						break;
					}
				if (flag) {
					ItemStack baseItemCopy = baseItem.copy();
					if (pearl.hasTagCompound()) baseItemCopy.setTagCompound(pearl.getTagCompound());
					return baseItemCopy;
				}
				return null;
			}
		} else if (baseItem.getItemDamage() == 1) {
			ItemStack newPearl = new ItemStack(ModItems.PEARL_NACRE);
			if (baseItem.hasTagCompound()) newPearl.setTagCompound(baseItem.getTagCompound());
			baseItem.setItemDamage(0);
			return newPearl;
		}
		return null;
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
