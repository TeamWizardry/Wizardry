package com.teamwizardry.wizardry.crafting.mana;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.crafting.mana.FluidRecipeBuilder.FluidCrafter;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class FluidRecipeLoader {
	public static final FluidRecipeLoader INSTANCE = new FluidRecipeLoader();

	private File directory;

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	@SuppressWarnings("deprecation")
	public void processRecipes(Map<String, FluidCrafter> recipeRegistry, Multimap<Ingredient, FluidCrafter> recipes) {
		Wizardry.LOGGER.info("<<========================================================================>>");
		Wizardry.LOGGER.info("> Starting fluid recipe loading.");

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

		fileLoop:
		for (File file : recipeFiles) {
			try {
				if (!file.exists()) {
					Wizardry.LOGGER.error("  > SOMETHING WENT WRONG! " + file.getPath() + " can NOT be found. Ignoring file...");
					continue;
				}

				JsonElement element;
				try {
					element = new JsonParser().parse(new FileReader(file));
				} catch (FileNotFoundException e) {
					Wizardry.LOGGER.error("  > SOMETHING WENT WRONG! " + file.getPath() + " can NOT be found. Ignoring file...");
					continue;
				}

				if (element == null) {
					Wizardry.LOGGER.error("  > SOMETHING WENT WRONG! Could not parse " + file.getPath() + ". Ignoring file...");
					continue;
				}

				if (!element.isJsonObject()) {
					Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT contain a JsonObject. Ignoring file...: " + element.toString());
					continue;
				}

				JsonObject fileObject = element.getAsJsonObject();

				List<Ingredient> extraInputs = new LinkedList<>();
				Fluid fluid = ModFluids.MANA.getActual();
				int duration = 100;
				int required = 1;
				boolean consume = false;
				boolean explode = false;
				boolean bubbling = true;
				boolean harp = true;
				boolean instant = true;

				if (recipeRegistry.containsKey(file.getPath())) {
					Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " already exists in the recipe map. Ignoring file...: " + element.toString());
					continue;
				}

				if (!fileObject.has("output")) {
					Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT specify a recipe output. Ignoring file...: " + element.toString());
					continue;
				}

				if (!fileObject.get("output").isJsonObject()) {
					Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output. Ignoring file...: " + element.toString());
					continue;
				}

				if (!fileObject.has("input")) {
					Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT provide an initial input item. Ignoring file...: " + element.toString());
					continue;
				}

				JsonElement inputObject = fileObject.get("input");
				Ingredient inputItem = CraftingHelper.getIngredient(inputObject, context);

				if (inputItem == Ingredient.EMPTY) {
					Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT provide a valid input item. Ignoring file...: " + element.toString());
					continue;
				}

				if (fileObject.has("extraInputs")) {
					if (!fileObject.get("extraInputs").isJsonArray()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " has extra inputs NOT in a JsonArray format. Ignoring file...: " + element.toString());
						continue;
					}
					JsonArray extraInputArray = fileObject.get("extraInputs").getAsJsonArray();
					for (JsonElement extraInput : extraInputArray) {
						Ingredient ingredient = CraftingHelper.getIngredient(extraInput, context);
						if (ingredient == Ingredient.EMPTY) {
							Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT provide a valid extra input item. Ignoring file...: " + element.toString());
							continue fileLoop;
						}
						extraInputs.add(ingredient);
					}
				}

				if (fileObject.has("fluid")) {
					if (!fileObject.get("fluid").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("fluid").isString()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT give rfluid as a string. Ignoring file...: " + element.toString());
						continue;
					}
					fluid = FluidRegistry.getFluid(fileObject.get("fluid").getAsString());
				}

				if (fileObject.has("duration")) {
					if (!fileObject.get("duration").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("duration").isNumber()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT give duration as a number. Ignoring file...:" + element.toString());
						continue;
					}
					duration = fileObject.get("duration").getAsInt();
				}

				if (fileObject.has("required")) {
					if (!fileObject.get("required").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("required").isNumber()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT give required as a number. Ignoring file...: " + element.toString());
						continue;
					}
					required = fileObject.get("required").getAsInt();
				}

				if (fileObject.has("consume")) {
					if (!fileObject.get("consume").isJsonPrimitive()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT give consume as a boolean. Ignoring file...: " + element.toString());
						continue;
					}
					consume = fileObject.get("consume").getAsBoolean();
				}

				if (fileObject.has("explode")) {
					if (!fileObject.get("explode").isJsonPrimitive()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT give explode as a boolean. Ignoring file...: " + element.toString());
						continue;
					}
					explode = fileObject.get("explode").getAsBoolean();
				}

				if (fileObject.has("harp")) {
					if (!fileObject.get("harp").isJsonPrimitive()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT give harp as a boolean. Ignoring file...: " + element.toString());
						continue;
					}
					harp = fileObject.get("harp").getAsBoolean();
				}

				if (fileObject.has("bubbling")) {
					if (!fileObject.get("bubbling").isJsonPrimitive()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT give bubbling as a boolean. Ignoring file...: " + element.toString());
						continue;
					}
					bubbling = fileObject.get("bubbling").getAsBoolean();
				}
				
				if (fileObject.has("instant")) {
					if (!fileObject.get("instant").isJsonPrimitive()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT give instant as a boolean. Ignoring file...: " + element.toString());
						continue;
					}
					instant = fileObject.get("instant").getAsBoolean();
				}

				JsonElement typeElement = fileObject.get("type");
				String type = typeElement == null ? "item" : typeElement.getAsString();
				JsonObject output = fileObject.get("output").getAsJsonObject();

				if (type.equalsIgnoreCase("item")) {
					ItemStack outputItem = CraftingHelper.getItemStack(output, context);

					if (outputItem.isEmpty()) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output item. Ignoring file...: " + element.toString());
						continue;
					}

					FluidCrafter build = FluidRecipeBuilder.buildFluidCrafter(file.getPath(), outputItem, inputItem, extraInputs, fluid, duration, required, consume, explode, bubbling, harp, instant);
					recipeRegistry.put(file.getPath(), build);
					recipes.put(inputItem, build);
				} else if (type.equalsIgnoreCase("block")) {
					IBlockState outputBlock;

					JsonElement name = output.get("item");
					if (name == null)
						name = output.get("block");
					if (name == null)
						name = output.get("name");

					Block block = name != null ? ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name.getAsString())) : null;
					if (block == null) {
						Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output block. Ignoring file...: " + element.toString());
						continue;
					}

					int meta = 0;

					JsonElement data = output.get("data");
					if (data == null)
						data = output.get("meta");

					if (data != null && data.isJsonPrimitive() && data.getAsJsonPrimitive().isNumber())
						meta = data.getAsInt();
					outputBlock = block.getStateFromMeta(meta);

					FluidCrafter build = FluidRecipeBuilder.buildFluidCrafter(file.getPath(), outputBlock, inputItem, extraInputs, fluid, duration, required, consume, explode, bubbling, harp);
					recipeRegistry.put(file.getPath(), build);
					recipes.put(inputItem, build);
				} else
					Wizardry.LOGGER.error("  > WARNING! " + file.getPath() + " specifies an invalid recipe output type. Valid recipe types: \"item\" \"block\". Ignoring file...: " + element.toString());
			} catch (Exception jsonException) {
				Wizardry.LOGGER.error("  > WARNING! Skipping " + file.getPath() + " due to error: ", jsonException);
			}
		}
		Wizardry.LOGGER.info("> Finished mana recipe loading.");
		Wizardry.LOGGER.info("<<========================================================================>>");
	}
}
