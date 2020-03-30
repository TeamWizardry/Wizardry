package com.teamwizardry.wizardry.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;

import java.util.function.Consumer;

public class RecipesProvider extends net.minecraft.data.RecipeProvider {
	public RecipesProvider(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {

	}

	@Override
	public String getName() {
		return "Wizardry crafting recipes";
	}
}
