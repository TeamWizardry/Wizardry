package com.teamwizardry.wizardry.crafting;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RecipeShapedFluidFactory implements IRecipeFactory {
	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		String group = JsonUtils.getString(json, "group", "");

		Map<Character, Ingredient> ingredientMap = new HashMap<>();
		ingredientMap.put(' ', Ingredient.EMPTY);

		for (Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
			if (entry.getKey().length() != 1)
				throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			if (" ".equals(entry.getKey()))
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

			ingredientMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
		}

		JsonArray jsonPattern = JsonUtils.getJsonArray(json, "pattern");

		if (jsonPattern.size() == 0)
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

		String[] pattern = new String[jsonPattern.size()];
		for (int x = 0; x < pattern.length; x++) {
			String line = JsonUtils.getString(jsonPattern.get(x), "pattern[" + x + "]");
			if (x > 0 && pattern[0].length() != line.length())
				throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
			pattern[x] = line;
		}

		ShapedPrimer primer = new ShapedPrimer();
		primer.width = pattern[0].length();
		primer.height = pattern.length;
		primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
		primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

		Set<Character> keys = Sets.newHashSet(ingredientMap.keySet());
		keys.remove(' ');

		int x = 0;
		for (String line : pattern) {
			for (char c : line.toCharArray()) {
				Ingredient ingredient = ingredientMap.get(c);
				if (ingredient == null)
					throw new JsonSyntaxException("Pattern references symbol '" + c + "' but it's not defined in the key");
				primer.input.set(x++, ingredient);
				keys.remove(c);
			}
		}

		if (!keys.isEmpty())
			throw new JsonSyntaxException("Key defineds symbols that aren't used in pattern: " + keys);

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
		RecipeShapedFluid recipe = new RecipeShapedFluid(group.isEmpty() ? null : new ResourceLocation(group), result, primer);

		return recipe;
	}
}
