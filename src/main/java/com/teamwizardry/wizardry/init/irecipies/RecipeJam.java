package com.teamwizardry.wizardry.init.irecipies;

import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import com.teamwizardry.wizardry.init.ModItems;

/**
 * Created by Saad on 8/30/2016.
 */
public class RecipeJam implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		boolean foundJar = false;
		boolean foundSword = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (stack.getItem() == ModItems.JAR) {

					if (stack.getItemDamage() == 1)
						foundJar = true;
				}
				if (stack.getItem() == Items.GOLDEN_SWORD)
					foundSword = true;
			}
		}
		return foundJar && foundSword;
	}

	@Nullable
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack sword = null;
		ItemStack jar = null;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (stack.getItem() == ModItems.JAR) {
					if (stack.getItemDamage() == 1)
						jar = stack;
				}
				if (stack.getItem() == Items.GOLDEN_SWORD)
					sword = stack;
			}
		}

		if (sword == null || jar == null)
			return null;

		ItemStack baseItemCopy = jar.copy();
		baseItemCopy.setItemDamage(2);
		if (sword.hasTagCompound()) baseItemCopy.setTagCompound(sword.getTagCompound());

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

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		ItemStack[] remainingItems = ForgeHooks.defaultRecipeGetRemainingItems(inv);
		
		ItemStack sword;
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null && stack.getItem() == Items.GOLDEN_SWORD)
			{
				sword = stack.copy();
				sword.setItemDamage(sword.getItemDamage() + 1);
				if (sword.getItemDamage() > sword.getMaxDamage()) sword = null;
				remainingItems[i] = sword;
				break;
			}
		}
		
		return remainingItems;
	}
}
