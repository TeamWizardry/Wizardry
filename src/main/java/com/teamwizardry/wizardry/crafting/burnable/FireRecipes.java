package com.teamwizardry.wizardry.crafting.burnable;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.item.crafting.Ingredient;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class FireRecipes {
	public static final FireRecipes INSTANCE = new FireRecipes();

	public static final HashMap<Ingredient, FireRecipe> RECIPES = new HashMap<>();

	private static final String[] INTERNAL_RECIPE_NAMES = {"devil_dust", "devil_dust_secondary", "sky_dust"};

	public void loadRecipes(File directory) {
		FireRecipeLoader.INSTANCE.setDirectory(directory);
		FireRecipeLoader.INSTANCE.processRecipes(RECIPES);
	}

	public void copyMissingRecipes(File directory) {
		for (String recipeName : INTERNAL_RECIPE_NAMES) {
			File file = new File(directory, recipeName + ".json");
			if (file.exists()) continue;

			InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "fire_recipes/" + recipeName + ".json");
			if (stream == null) {
				Wizardry.logger.fatal("    > SOMETHING WENT WRONG! Could not read recipe " + recipeName + " from mod jar! Report this to the devs on Github!");
				continue;
			}

			try {
				FileUtils.copyInputStreamToFile(stream, file);
				Wizardry.logger.info("    > Mana recipe " + recipeName + " copied successfully from mod jar.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
