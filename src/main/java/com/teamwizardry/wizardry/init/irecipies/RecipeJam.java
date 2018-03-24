package com.teamwizardry.wizardry.init.irecipies;

import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/30/2016.
 */
public class RecipeJam extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		boolean foundJar = false;
		boolean foundSword = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == ModItems.JAR) {

				if (stack.getItemDamage() == 1)
					foundJar = true;
			}
			if (stack.getItem() == Items.GOLDEN_SWORD)
				foundSword = true;
		}
		return foundJar && foundSword;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack sword = ItemStack.EMPTY;
		ItemStack jar = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == ModItems.JAR) {
				if (stack.getItemDamage() == 1)
					jar = stack;
			}
			if (stack.getItem() == Items.GOLDEN_SWORD)
				sword = stack;
		}

		ItemStack baseItemCopy = jar.copy();
		baseItemCopy.setItemDamage(2);
		if (sword.hasTagCompound()) baseItemCopy.setTagCompound(sword.getTagCompound());

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
		NonNullList<ItemStack> remainingItems = ForgeHooks.defaultRecipeGetRemainingItems(inv);

		ItemStack sword;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == Items.GOLDEN_SWORD) {
				sword = stack.copy();
				sword.setItemDamage(sword.getItemDamage() + 1);
				if (sword.getItemDamage() > sword.getMaxDamage()) sword = null;
				if (sword != null) {
					remainingItems.set(i, sword);
				}
				break;
			}
		}

		return remainingItems;
	}
}
