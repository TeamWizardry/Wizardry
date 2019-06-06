package com.teamwizardry.wizardry.api;

import com.teamwizardry.wizardry.api.block.ICraftingPlateRecipe;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.common.core.craftingplaterecipes.FairyJarRecipe;
import com.teamwizardry.wizardry.common.core.craftingplaterecipes.PearlInfusionRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public final class CraftingPlateRecipeManager {

	private static final Set<ICraftingPlateRecipe> recipes = new HashSet<>();

	static {
		addRecipe(new PearlInfusionRecipe());
		addRecipe(new FairyJarRecipe());
	}

	private CraftingPlateRecipeManager() {
	}

	@Nullable
	public static ICraftingPlateRecipe getRecipeForItem(ItemStack stack) {
		for (ICraftingPlateRecipe recipe : recipes) {
			if (recipe.doesRecipeExistForItem(stack)) {
				return recipe;
			}
		}
		return null;
	}

	@Nullable
	public static ICraftingPlateRecipe getRecipe(World world, BlockPos pos, ItemStack stack) {
		for (ICraftingPlateRecipe recipe : recipes) {
			if (recipe.doesRecipeExistForItem(stack) || recipe.doesRecipeExistInWorld(world, pos)) {
				return recipe;
			}
		}
		return null;
	}

	public static void addRecipe(ICraftingPlateRecipe recipe) {
		recipes.add(recipe);
	}

	public static boolean doesRecipeExistForItem(ItemStack stack) {
		for (ICraftingPlateRecipe recipe : recipes) {
			if (recipe.doesRecipeExistForItem(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean doesRecipeExist(World world, BlockPos pos, ItemStack stack) {
		for (ICraftingPlateRecipe recipe : recipes) {
			if (recipe.doesRecipeExistInWorld(world, pos) || recipe.doesRecipeExistForItem(stack)) {
				return true;
			}
		}
		return false;
	}

	public static boolean tick(World world, BlockPos pos, ItemStack input, ItemStackHandler inventoryHandler, Function<IWizardryCapability, Double> consumeMana) {
		ICraftingPlateRecipe recipe = null;

		for (ICraftingPlateRecipe search : recipes) {
			if (search.doesRecipeExistInWorld(world, pos) || search.doesRecipeExistForItem(input)) {
				recipe = search;
				break;
			}
		}
		if (recipe == null) return false;

		if (!recipe.isDone(world, pos, input)) {
			recipe.tick(world, pos, input, inventoryHandler, consumeMana);
		} else {
			recipe.complete(world, pos, input, inventoryHandler);
			return true;
		}
		return false;
	}

}
