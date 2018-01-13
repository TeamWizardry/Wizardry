package com.teamwizardry.wizardry.crafting.burnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.wizardry.Wizardry;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class FireRecipeLoader
{
	public static final FireRecipeLoader INSTANCE = new FireRecipeLoader();
	
	private File directory;
	
	public void setDirectory(File directory)
	{
		this.directory = directory;
	}
	
	public void processRecipes(Map<ItemStack, FireRecipe> itemRecipes, Map<String, FireRecipe> oredictRecipes)
	{
		Wizardry.logger.info("<<========================================================================>>");
		Wizardry.logger.info("> Starting fire recipe loading.");
		
		LinkedList<File> recipeFiles = new LinkedList<>();
		Stack<File> toProcess = new Stack<>();
		toProcess.push(directory);
		
		while (!toProcess.isEmpty())
		{
			File file = toProcess.pop();
			if (file.isDirectory())
			{
				File[] children = file.listFiles();
				for (File child : children)
					toProcess.push(child);
			}
			else if (file.isFile())
				if (file.getName().endsWith(".json"))
					recipeFiles.add(file);
		}
		
		for (File file : recipeFiles)
		{
			if (!file.exists())
			{
				Wizardry.logger.error("  > SOMETHING WENT WRONG! " + file.getPath() + " can NOT be found. Ignoring file...");
				continue;
			}
			
			JsonElement element;
			try
			{
				element = new JsonParser().parse(new FileReader(file));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				continue;
			}
			
			if (element == null)
			{
				Wizardry.logger.error("  > SOMETHING WENT WRONG! Could not parse " + file.getPath() + ". Ignoring file...");
				continue;
			}
			
			if (!element.isJsonObject())
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT contain a JsonObject. Ignoring file...: " + element.toString());
				continue;
			}
			
			JsonObject fileObject = element.getAsJsonObject();
			
			ItemStack input = null;
			String oreIn = null;
			ItemStack output = null;
			int duration = 200;
			
			if (!fileObject.has("input"))
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide an initial input item. Ignoring file...: " + element.toString());
				continue;
			}
			
			if (!fileObject.get("input").isJsonObject())
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide an initial input item as a JsonObject. Ignoring file...: " + element.toString());
				continue;
			}
			
			JsonObject inputObject = fileObject.get("input").getAsJsonObject();
			if (inputObject.has("name"))
			{
				Item in = ForgeRegistries.ITEMS.getValue(new ResourceLocation(inputObject.get("name").getAsString()));
				if (in == null)
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid initial input item. Ignoring file...: " + element.toString());
					continue;
				}
				if (in == Items.REDSTONE && Loader.isModLoaded("fluxnetworks"))
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " gives an input of Redstone Dust, which is incompatible with Flux Networks. Ignoring file...: " + element.toString());
					continue;
				}
				int metaIn = 0;
				if (inputObject.has("meta") && inputObject.get("meta").isJsonPrimitive() && inputObject.getAsJsonPrimitive("meta").isNumber())
					metaIn = inputObject.get("meta").getAsInt();
				input = new ItemStack(in, 1, metaIn);
				if (FireRecipes.ITEM_RECIPES.containsKey(input))
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " provides an already used input item. Ignoring file...: " + element.toString());
					continue;
				}
			}
			else if (inputObject.has("oredict"))
			{
				if (OreDictionary.doesOreNameExist(inputObject.get("oredict").getAsString()))
					oreIn = inputObject.get("oredict").getAsString();
				if (FireRecipes.OREDICT_RECIPES.containsKey(oreIn))
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " provides an already used input oredict. Ignoring file...: " + element.toString());
					continue;
				}
				if (OreDictionary.getOres(oreIn, false).stream().map(stack -> stack.getItem()).anyMatch(item -> item == Items.REDSTONE))
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " gives an input OreDict that contains Redstone Dust, which is incompatible with Flux Networks. Recipe may not function correctly.");
			}
			else
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a name or oredict for the initial input item. Ignoring file...: " + element.toString());
				continue;
			}
			
			if (!fileObject.has("output"))
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT specify a recipe output. Ignoring file...: " + element.toString());
				continue;
			}
			
			if (!fileObject.get("output").isJsonObject())
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output. Ignoring file...: " + element.toString());
				continue;
			}
			
			JsonObject outputObject = fileObject.get("output").getAsJsonObject();
			if (outputObject.has("name"))
			{
				Item out = ForgeRegistries.ITEMS.getValue(new ResourceLocation(outputObject.get("name").getAsString()));
//				if (out == null)
//				{
//					Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(outputObject.get("name").getAsString()));
//					if (block != null)
//						out = ItemBlock.getItemFromBlock(block);
//				}
				if (out == null)
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid initial output item. Ignoring file...: " + element.toString());
					continue;
				}
				int metaOut = 0;
				if (outputObject.has("meta") && outputObject.get("meta").isJsonPrimitive() && outputObject.getAsJsonPrimitive("meta").isNumber())
					metaOut = outputObject.get("meta").getAsInt();
				int count = 1;
				if (outputObject.has("count") && outputObject.get("count").isJsonPrimitive() && outputObject.getAsJsonPrimitive("count").isNumber())
					count = outputObject.get("count").getAsInt();
				output = new ItemStack(out, count, metaOut);
			}
			
			if (fileObject.has("duration"))
			{
				if (!fileObject.get("duration").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("duration").isNumber())
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give duration as a number. Ignoring file...:" + element.toString());
					continue;
				}
				duration = fileObject.get("duration").getAsInt();
			}
			
			if (oreIn == null)
				itemRecipes.put(input, new FireRecipe(output, duration));
			else
				oredictRecipes.put(oreIn, new FireRecipe(output, duration));
		}
		Wizardry.logger.info("> Finished fire recipe loading.");
		Wizardry.logger.info("<<========================================================================>>");
	}
}
