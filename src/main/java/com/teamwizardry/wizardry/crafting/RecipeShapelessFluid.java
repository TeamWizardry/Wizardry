package com.teamwizardry.wizardry.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Iterator;

public class RecipeShapelessFluid extends ShapelessOreRecipe {
	public RecipeShapelessFluid(ResourceLocation group, ItemStack result, NonNullList<Ingredient> input) {
		super(group, input, result);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> remains = super.getRemainingItems(inv);
		for (int i = 0; i < remains.size(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			ItemStack remain = remains.get(i);
			if (!stack.isEmpty() && remain.isEmpty() && stack.getItem() instanceof UniversalBucket) {
				ItemStack empty = ((UniversalBucket) stack.getItem()).getEmpty();
				if (!empty.isEmpty())
					remains.set(i, empty.copy());
			}
		}
		return remains;
	}

	@Override
	public boolean matches(InventoryCrafting matrix, World world) {
		ArrayList<Ingredient> required = new ArrayList<>(getIngredients());

		for (int i = 0; i < matrix.getSizeInventory(); i++) {
			ItemStack slot = matrix.getStackInSlot(i);
			if (!slot.isEmpty()) {
				boolean inRecipe = false;
				Iterator<Ingredient> iterator = required.iterator();
				while (iterator.hasNext()) {
					Ingredient next = iterator.next();
					if (next.apply(slot)) {
						inRecipe = true;
						iterator.remove();
						break;
					}
				}
				if (!inRecipe)
					return false;
			}
		}
		return required.isEmpty();
	}
}
