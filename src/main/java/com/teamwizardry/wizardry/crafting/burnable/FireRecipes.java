package com.teamwizardry.wizardry.crafting.burnable;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.teamwizardry.wizardry.crafting.mana.ManaRecipes.getResourceListing;

public class FireRecipes {
	public static final FireRecipes INSTANCE = new FireRecipes();

	public static final HashMap<Ingredient, FireRecipe> RECIPES = new HashMap<>();

	public void loadRecipes(File directory) {
		FireRecipeLoader.INSTANCE.setDirectory(directory);
		FireRecipeLoader.INSTANCE.processRecipes(RECIPES);
	}

	public void copyMissingRecipes(File directory) {
		for (String recipeName : getResourceListing(Wizardry.MODID, "fire_recipes")) {
			if (recipeName.isEmpty()) continue;

			File file = new File(directory, recipeName);
			if (file.exists()) continue;

			InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "fire_recipes/" + recipeName);
			if (stream == null) {
				Wizardry.LOGGER.fatal("    > SOMETHING WENT WRONG! Could not read recipe " + recipeName + " from mod jar! Report this to the devs on Github!");
				continue;
			}

			try {
				FileUtils.copyInputStreamToFile(stream, file);
				Wizardry.LOGGER.info("    > Fire recipe " + recipeName + " copied successfully from mod jar.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void copyAllRecipes(File directory)
	{
		Map<String, ModContainer> modList = Loader.instance().getIndexedModList();
		for (Map.Entry<String, ModContainer> entry : modList.entrySet() ) {
			for (String recipeName : getResourceListing(entry.getKey(), "fire_recipes")) {
				if (recipeName.isEmpty()) continue;
	
				InputStream stream = LibrarianLib.PROXY.getResource(entry.getKey(), "fire_recipes/" + recipeName);
				if (stream == null) {
					Wizardry.LOGGER.fatal("    > SOMETHING WENT WRONG! Could not read recipe " + recipeName + " from mod jar of '" + entry.getKey() + "'! Report this to the devs on Github!");
					continue;
				}
				
				try {
					FileUtils.copyInputStreamToFile(stream, new File(directory, recipeName));
					Wizardry.LOGGER.info("    > Fire recipe " + recipeName + " copied successfully from mod jar.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
