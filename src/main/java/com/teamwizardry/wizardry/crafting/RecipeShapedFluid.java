package com.teamwizardry.wizardry.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeShapedFluid extends ShapedOreRecipe {
	public RecipeShapedFluid(ResourceLocation loc, ItemStack result, ShapedPrimer primer) {
		super(loc, result, primer);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> remains = ForgeHooks.defaultRecipeGetRemainingItems(inv);
		for (int i = 0; i < height * width; i++) {
			ItemStack stack = inv.getStackInSlot(i);
			NonNullList<Ingredient> matchedIngredients = this.input;
			if (matchedIngredients.get(i) instanceof IngredientFluidStack) {
				if (!stack.isEmpty()) {
					ItemStack copy = stack.copy();
					copy.setCount(1);
					remains.set(i, copy);
				}
				IFluidHandlerItem handler = FluidUtil.getFluidHandler(remains.get(i));
				if (handler != null) {
					FluidStack fluid = ((IngredientFluidStack) matchedIngredients.get(i)).getFluid();
					handler.drain(fluid.amount, true);
					remains.set(i, handler.getContainer());
				}
			}
		}
		return remains;
	}
}
