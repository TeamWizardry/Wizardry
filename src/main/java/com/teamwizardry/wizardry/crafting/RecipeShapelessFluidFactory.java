package com.teamwizardry.wizardry.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class RecipeShapelessFluidFactory implements IRecipeFactory {
	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		String group = JsonUtils.getString(json, "group", "");
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for (JsonElement element : JsonUtils.getJsonArray(json, "ingredients"))
			ingredients.add(CraftingHelper.getIngredient(element, context));

		if (ingredients.isEmpty())
			throw new JsonParseException("No ingredients in shapeless recipe");

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
		RecipeShapelessFluid recipe = new RecipeShapelessFluid(group.isEmpty() ? null : new ResourceLocation(group), result, ingredients);

		return recipe;
	}
}
