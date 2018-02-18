package com.teamwizardry.wizardry.crafting.burnable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

public class FireRecipeLoader {
	public static final FireRecipeLoader INSTANCE = new FireRecipeLoader();

	private File directory;

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	public void processRecipes(Map<Ingredient, FireRecipe> recipes) {
		Wizardry.logger.info("<<========================================================================>>");
		Wizardry.logger.info("> Starting fire recipe loading.");

		JsonContext context = new JsonContext("minecraft");

		LinkedList<File> recipeFiles = new LinkedList<>();
		Stack<File> toProcess = new Stack<>();
		toProcess.push(directory);

		while (!toProcess.isEmpty()) {
			File file = toProcess.pop();
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				if (children != null) for (File child : children)
					toProcess.push(child);
			} else if (file.isFile())
				if (file.getName().endsWith(".json"))
					recipeFiles.add(file);
		}

		for (File file : recipeFiles) {
			try {
				if (!file.exists()) {
					Wizardry.logger.error("  > SOMETHING WENT WRONG! " + file.getPath() + " can NOT be found. Ignoring file...");
					continue;
				}

				JsonElement element;
				try {
					element = new JsonParser().parse(new FileReader(file));
				} catch (FileNotFoundException e) {
					Wizardry.logger.error("  > SOMETHING WENT WRONG! " + file.getPath() + " can NOT be found. Ignoring file...");
					continue;
				}

				if (element == null) {
					Wizardry.logger.error("  > SOMETHING WENT WRONG! Could not parse " + file.getPath() + ". Ignoring file...");
					continue;
				}

				if (!element.isJsonObject()) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT contain a JsonObject. Ignoring file...: " + element.toString());
					continue;
				}

				JsonObject fileObject = element.getAsJsonObject();

				int duration = 200;

				if (!fileObject.has("input")) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide an initial input item. Ignoring file...: " + element.toString());
					continue;
				}

				JsonElement inputObject = fileObject.get("input");
				Ingredient input = CraftingHelper.getIngredient(inputObject, context);

				if (input == Ingredient.EMPTY) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid input. Ignoring file...: " + element.toString());
					continue;
				}

				if (!fileObject.has("output")) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT specify a recipe output. Ignoring file...: " + element.toString());
					continue;
				}

				if (!fileObject.get("output").isJsonObject()) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output. Ignoring file...: " + element.toString());
					continue;
				}

				JsonObject outputObject = fileObject.get("output").getAsJsonObject();
				ItemStack output = CraftingHelper.getItemStack(outputObject, context);

				if (output.isEmpty()) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output. Ignoring file...: " + element.toString());
					continue;
				}

				if (fileObject.has("duration")) {
					if (!fileObject.get("duration").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("duration").isNumber()) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give duration as a number. Ignoring file...:" + element.toString());
						continue;
					}
					duration = fileObject.get("duration").getAsInt();
				}

				recipes.put(input, new FireRecipe(output, duration));
			} catch (JsonParseException jsonException) {
				Wizardry.logger.error("  > WARNING! Skipping " + file.getPath() + " due to error: ", jsonException);
			}
		}
		Wizardry.logger.info("> Finished fire recipe loading.");
		Wizardry.logger.info("<<========================================================================>>");
	}
}
