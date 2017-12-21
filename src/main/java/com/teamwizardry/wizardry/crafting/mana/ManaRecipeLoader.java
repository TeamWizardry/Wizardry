package com.teamwizardry.wizardry.crafting.mana;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModSounds;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class ManaRecipeLoader
{
	public static final ManaRecipeLoader INSTANCE = new ManaRecipeLoader();
	
	private File directory;
	
	public void setDirectory(File directory)
	{
		this.directory = directory;
	}
	
	@SuppressWarnings("deprecation")
	public void processRecipes(Map<String, ManaCrafterBuilder> recipeRegistry, HashMultimap<ItemStack, ManaCrafterBuilder> recipes, HashMultimap<String, ManaCrafterBuilder> oredictRecipes)
	{
		Wizardry.logger.info("<<========================================================================>>");
		Wizardry.logger.info("> Starting mana recipe loading.");
		
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
			
			ItemStack inputItem = null;
			String inputOredict = null;
			List<ItemStack> extraInputItems = new LinkedList<>();
			List<String> extraInputOredicts = new LinkedList<>();
			int duration = 200;
			int radius = 0;
			boolean consume = false;
			boolean explode = false;
			boolean silent = false;
			
			if (recipes.containsKey(file.getPath()))
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " already exists in the recipe map. Ignoring file...: " + element.toString());
				continue;
			}
			
			if (!fileObject.has("type"))
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT specify a recipe output type. Ignoring file...: " + element.toString());
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
				int metaIn = 0;
				if (inputObject.has("meta") && inputObject.get("meta").isJsonPrimitive() && inputObject.getAsJsonPrimitive("meta").isNumber())
					metaIn = inputObject.get("meta").getAsInt();
				inputItem = new ItemStack(in, 1, metaIn);
			}
			else if (inputObject.has("oredict"))
			{
				if (OreDictionary.doesOreNameExist(inputObject.get("oredict").getAsString()))
					inputOredict = inputObject.get("oredict").getAsString();	
			}
			else
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a name or oredict for the initial input item. Ignoring file...: " + element.toString());
				continue;
			}
			
			if (fileObject.has("extraInputs"))
			{
				if (!fileObject.get("extraInputs").isJsonArray())
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " has extra inputs NOT in a JsonArray format. Ignoring file...: " + element.toString());
					continue;
				}
				JsonArray extraInputs = fileObject.get("extraInputs").getAsJsonArray();
				for (JsonElement extraInput : extraInputs)
				{
					if (!extraInput.isJsonObject())
					{
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " has an extra input in the array NOT in JsonObject format. Ignoring file...: " + element.toString());
						continue;
					}
					JsonObject extraIn = extraInput.getAsJsonObject();
					if (extraIn.has("name"))
					{
						Item in = ForgeRegistries.ITEMS.getValue(new ResourceLocation(extraIn.get("name").getAsString()));
						if (in == null)
						{
							Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid extra input item. Ignoring file...: " + element.toString());
							continue;
						}
						int metaIn = 0;
						if (extraIn.has("meta") && extraIn.get("meta").isJsonPrimitive() && extraIn.getAsJsonPrimitive("meta").isNumber())
							metaIn = extraIn.get("meta").getAsInt();
						extraInputItems.add(new ItemStack(in, 1, metaIn));
					}
					else if (extraIn.has("oredict"))
					{
						if (OreDictionary.doesOreNameExist(extraIn.get("oredict").getAsString()))
							extraInputOredicts.add(inputObject.get("oredict").getAsString());
					}
					else
					{
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a name ore oredict for an extra input item. Ignoring file...: " + element.toString());
						continue;
					}
				}
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
			
			if (fileObject.has("radius"))
			{
				if (!fileObject.get("radius").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("radius").isNumber())
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give radius as a number. Ignoring file...: " + element.toString());
					continue;
				}
				radius = fileObject.get("radius").getAsInt();
			}
			
			if (fileObject.has("consume"))
			{
				if (!fileObject.get("consume").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("consume").isBoolean())
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give consume as a boolean. Ignoring file...: " + element.toString());
					continue;
				}
				consume = fileObject.get("consume").getAsBoolean();
			}
			
			if (fileObject.has("explode"))
			{
				if (!fileObject.get("explode").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("explode").isBoolean())
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give explode as a boolean. Ignoring file...: " + element.toString());
					continue;
				}
				explode = fileObject.get("explode").getAsBoolean();
			}
			
			if (fileObject.has("silent"))
			{
				if (!fileObject.get("silent").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("silent").isBoolean())
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give silent as a boolean. Ignoring file...: " + element.toString());
					continue;
				}
				silent = fileObject.get("silent").getAsBoolean();
			}
			
			String type = fileObject.get("type").getAsString();
			JsonObject output = fileObject.get("output").getAsJsonObject();
			if (!output.has("name"))
			{
				Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide the name of the recipe output. Ignoring file...: " + element.toString());
				continue;
			}

			if (type.equalsIgnoreCase("item"))
			{
				ItemStack outputItem;
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(output.get("name").getAsString()));
				if (item == null)
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output item. Ignoring file...: " + element.toString());
					continue;
				}
				int meta = 0;
				if (output.has("meta") && output.get("meta").isJsonPrimitive() && output.getAsJsonPrimitive("meta").isNumber())
					meta = output.get("meta").getAsInt();
				meta = MathHelper.clamp(meta, 0, 15);
				int count = 1;
				if (output.has("count") && output.get("count").isJsonPrimitive() && output.getAsJsonPrimitive("count").isNumber())
					count = output.get("count").getAsInt();
				outputItem = new ItemStack(item, 1, meta);
				count = MathHelper.clamp(count, 1, outputItem.getMaxStackSize());
				outputItem.setCount(count);
				if (output.has("nbt") && output.get("nbt").isJsonObject()) try
				{
					outputItem.setTagCompound(JsonToNBT.getTagFromJson(output.get("nbt").toString()));
				}
				catch (NBTException e)
				{
					e.printStackTrace();
					continue;
				}
			
				if (inputOredict == null)
				{
					ManaCrafterBuilder build = buildManaCrafter(file.getPath(), outputItem, inputItem, extraInputItems, extraInputOredicts, duration, radius, consume, explode, silent);
					recipeRegistry.put(file.getPath(), build);
					recipes.put(inputItem, build);
				}
				else
				{
					ManaCrafterBuilder build = buildManaCrafter(file.getPath(), outputItem, inputOredict, extraInputItems, extraInputOredicts, duration, radius, consume, explode, silent);
					recipeRegistry.put(file.getPath(), build);
					oredictRecipes.put(inputOredict, build);
				}
				continue;
			}
			if (type.equalsIgnoreCase("block"))
			{
				IBlockState outputBlock;
				Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(output.get("name").getAsString()));
				if (block == null)
				{
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output block. Ignoring file...: " + element.toString());
					continue;
				}
				int meta = 0;
				if (output.has("meta") && output.get("meta").isJsonPrimitive() && output.getAsJsonPrimitive("meta").isNumber())
					meta = output.get("meta").getAsInt();
				outputBlock = block.getStateFromMeta(meta);
			
				if (inputOredict == null)
				{
					ManaCrafterBuilder build = buildManaCrafter(file.getPath(), outputBlock, inputItem, extraInputItems, extraInputOredicts, duration, radius, consume, explode, silent);
					recipeRegistry.put(file.getPath(), build);
					recipes.put(inputItem, build);
				}
				else
				{
					ManaCrafterBuilder build = buildManaCrafter(file.getPath(), outputBlock, inputOredict, extraInputItems, extraInputOredicts, duration, radius, consume, explode, silent);
					recipeRegistry.put(file.getPath(), build);
					oredictRecipes.put(inputOredict, build);
				}
				continue;
			}

			Wizardry.logger.error("  > WARNING! " + file.getPath() + " specifies an invalid recipe output type. Valid recipe types: \"item\" \"block\". Ignoring file...: " + element.toString());
		}
	}
	
	private ManaCrafterBuilder buildManaCrafter(String identifier, ItemStack outputItem, ItemStack input, List<ItemStack> extraInputs, List<String> extraInputOredicts, int duration, int radius, boolean consume, boolean explode, boolean silent)
	{
		return new ManaCrafterBuilder(new ManaCrafterPredicate()
		{
			@Override
			public boolean check(World world, BlockPos pos, List<EntityItem> items)
			{
				if (radius > 0) for (int i = -radius; i <= radius; i++)
					for (int j = -radius; j <= radius; j++)
						if (world.getBlockState(pos.add(i, 0, j)) != ModBlocks.FLUID_MANA.getDefaultState()) return false;

				boolean foundInput = false;
				List<ItemStack> list = items.stream().map(entity -> entity.getItem()).collect(Collectors.toList());
				List<String> oredictList = new LinkedList<>();
				oredictList.addAll(extraInputOredicts);
				List<ItemStack> inputList = new LinkedList<>();
				inputList.addAll(extraInputs);
				for (ItemStack item : list)
				{
					if (foundInput && inputList.size() <= 0 && oredictList.size() <= 0) return true;
					if (!foundInput && ItemStack.areItemsEqual(item, input))
					{
						foundInput = true;
						continue;
					}
					if (inputList.size() > 0 && ItemStack.areItemsEqual(item, inputList.get(0)))
					{
						inputList.remove(0);
						continue;
					}
					if (oredictList.size() > 0 && OreDictionary.getOres(oredictList.get(0), false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, item)))
					{
						oredictList.remove(0);
						continue;
					}
				}
				return foundInput && inputList.size() <= 0 && oredictList.size() <= 0;
			}
		}, new ManaCrafterConsumer()
		{
			@Override
			public void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration)
			{
				EntityItem entityItem = items.stream().filter(entity -> ItemStack.areItemsEqual(entity.getItem(), input)).findFirst().orElse(null);
				if (world.isRemote) LibParticles.CRAFTING_ALTAR_IDLE(world, entityItem.getPositionVector());
				if (!silent && currentDuration % 40 == 0) world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
			}
		}, new ManaCrafterConsumer()
		{
			@Override
			public void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration)
			{
				if (consume) for (int i = -radius; i <= radius; i++)
					for (int j = -radius; j <= radius; j++)
						world.setBlockToAir(pos.add(i, 0, j));

				List<String> oredictList = new LinkedList<>();
				oredictList.addAll(extraInputOredicts);
				List<ItemStack> inputList = new LinkedList<>();
				inputList.addAll(extraInputs);
				inputList.add(input);

				while ((inputList.size() > 0 || oredictList.size() > 0) && items.size() > 0)
				{
					for (EntityItem entity : items)
					{
						if (inputList.size() > 0 && ItemStack.areItemsEqual(entity.getItem(), inputList.get(0)))
						{
							entity.getItem().shrink(1);
							if (entity.getItem().isEmpty()) world.removeEntity(entity);
							inputList.remove(0);
							continue;
						}
						if (oredictList.size() > 0 && OreDictionary.getOres(oredictList.get(0), false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, entity.getItem())))
						{
							entity.getItem().shrink(1);
							if (entity.getItem().isEmpty()) world.removeEntity(entity);
							oredictList.remove(0);
							continue;
						}
					}
				}

				EntityItem output = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, outputItem);
				output.motionX = 0;
				output.motionY = 0;
				output.motionZ = 0;
				output.forceSpawn = true;
				world.spawnEntity(output);

				if (explode)
				{
					PacketHandler.NETWORK.sendToAllAround(new PacketExplode(output.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true), new NetworkRegistry.TargetPoint(world.provider.getDimension(), output.posX, output.posY, output.posZ, 256));
					Utils.boom(world, output);
				}

				if (!silent) world.playSound(null, output.posX, output.posY, output.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
			}
		}, identifier, duration);
	}

	private ManaCrafterBuilder buildManaCrafter(String identifier, IBlockState outputBlock, ItemStack input, List<ItemStack> extraInputs, List<String> extraInputOredicts, int duration, int radius, boolean consume, boolean explode, boolean silent)
	{
		return new ManaCrafterBuilder(new ManaCrafterPredicate()
		{
			@Override
			public boolean check(World world, BlockPos pos, List<EntityItem> items)
			{
				if (radius > 0) for (int i = -radius; i <= radius; i++)
					for (int j = -radius; j <= radius; j++)
						if (world.getBlockState(pos.add(i, 0, j)) != ModBlocks.FLUID_MANA.getDefaultState()) return false;

				boolean foundInput = false;
				List<ItemStack> list = items.stream().map(entity -> entity.getItem()).collect(Collectors.toList());
				List<String> oredictList = new LinkedList<>();
				oredictList.addAll(extraInputOredicts);
				List<ItemStack> inputList = new LinkedList<>();
				inputList.addAll(extraInputs);
				for (ItemStack item : list)
				{
					if (foundInput && inputList.size() <= 0 && oredictList.size() <= 0) return true;
					if (!foundInput && ItemStack.areItemsEqual(item, input))
					{
						foundInput = true;
						continue;
					}
					if (inputList.size() > 0 && ItemStack.areItemsEqual(item, inputList.get(0)))
					{
						inputList.remove(0);
						continue;
					}
					if (oredictList.size() > 0 && OreDictionary.getOres(oredictList.get(0), false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, item)))
					{
						oredictList.remove(0);
						continue;
					}
				}
				return foundInput && inputList.size() <= 0 && oredictList.size() <= 0;
			}
		}, new ManaCrafterConsumer()
		{
			@Override
			public void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration)
			{
				EntityItem entityItem = items.stream().filter(entity -> ItemStack.areItemsEqual(entity.getItem(), input)).findFirst().orElse(null);
				if (world.isRemote) LibParticles.CRAFTING_ALTAR_IDLE(world, entityItem.getPositionVector());
				if (!silent && currentDuration % 40 == 0) world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
			}
		}, new ManaCrafterConsumer()
		{
			@Override
			public void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration)
			{
				if (consume) for (int i = -radius; i <= radius; i++)
					for (int j = -radius; j <= radius; j++)
						world.setBlockToAir(pos.add(i, 0, j));

				List<String> oredictList = new LinkedList<>();
				oredictList.addAll(extraInputOredicts);
				List<ItemStack> inputList = new LinkedList<>();
				inputList.addAll(extraInputs);
				inputList.add(input);

				while ((inputList.size() > 0 || oredictList.size() > 0) && items.size() > 0)
				{
					for (EntityItem entity : items)
					{
						if (inputList.size() > 0 && ItemStack.areItemsEqual(entity.getItem(), inputList.get(0)))
						{
							entity.getItem().shrink(1);
							if (entity.getItem().isEmpty()) world.removeEntity(entity);
							inputList.remove(0);
							continue;
						}
						if (oredictList.size() > 0 && OreDictionary.getOres(oredictList.get(0), false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, entity.getItem())))
						{
							entity.getItem().shrink(1);
							if (entity.getItem().isEmpty()) world.removeEntity(entity);
							oredictList.remove(0);
							continue;
						}
					}
				}

				world.setBlockState(pos, outputBlock);
				Vec3d output = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

				if (explode)
				{
					PacketHandler.NETWORK.sendToAllAround(new PacketExplode(output, Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true), new NetworkRegistry.TargetPoint(world.provider.getDimension(), output.x, output.y, output.z, 256));
					Utils.boom(world, output);
				}

				if (!silent) world.playSound(null, output.x, output.y, output.z, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
			}
		}, identifier, duration);
	}

	private ManaCrafterBuilder buildManaCrafter(String identifier, ItemStack outputItem, String inputOredict, List<ItemStack> extraInputs, List<String> extraInputOredicts, int duration, int radius, boolean consume, boolean explode, boolean silent)
	{
		return new ManaCrafterBuilder(new ManaCrafterPredicate()
		{
			@Override
			public boolean check(World world, BlockPos pos, List<EntityItem> items)
			{
				if (radius > 0) for (int i = -radius; i <= radius; i++)
					for (int j = -radius; j <= radius; j++)
						if (world.getBlockState(pos.add(i, 0, j)) != ModBlocks.FLUID_MANA.getDefaultState()) return false;

				boolean foundInput = false;
				List<ItemStack> list = items.stream().map(entity -> entity.getItem()).collect(Collectors.toList());
				List<String> oredictList = new LinkedList<>();
				oredictList.addAll(extraInputOredicts);
				List<ItemStack> inputList = new LinkedList<>();
				inputList.addAll(extraInputs);
				for (ItemStack item : list)
				{
					if (foundInput && inputList.size() <= 0 && oredictList.size() <= 0) return true;
					if (!foundInput && OreDictionary.getOres(inputOredict, false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, item)))
					{
						foundInput = true;
						continue;
					}
					if (inputList.size() > 0 && ItemStack.areItemsEqual(item, inputList.get(0)))
					{
						inputList.remove(0);
						continue;
					}
					if (oredictList.size() > 0 && OreDictionary.getOres(oredictList.get(0), false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, item)))
					{
						oredictList.remove(0);
						continue;
					}
				}
				return foundInput && inputList.size() <= 0 && oredictList.size() <= 0;
			}
		}, new ManaCrafterConsumer()
		{
			@Override
			public void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration)
			{
				EntityItem entityItem = items.stream().filter(entity -> OreDictionary.getOres(inputOredict, false).stream().anyMatch(ore -> ItemStack.areItemsEqual(entity.getItem(), ore))).findFirst().orElse(null);
				if (world.isRemote) LibParticles.CRAFTING_ALTAR_IDLE(world, entityItem.getPositionVector());
				if (!silent && currentDuration % 40 == 0) world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
			}
		}, new ManaCrafterConsumer()
		{
			@Override
			public void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration)
			{
				if (consume) for (int i = -radius; i <= radius; i++)
					for (int j = -radius; j <= radius; j++)
						world.setBlockToAir(pos.add(i, 0, j));

				List<String> oredictList = new LinkedList<>();
				oredictList.addAll(extraInputOredicts);
				List<ItemStack> inputList = new LinkedList<>();
				inputList.addAll(extraInputs);
				oredictList.add(inputOredict);

				while ((inputList.size() > 0 || oredictList.size() > 0) && items.size() > 0)
				{
					for (EntityItem entity : items)
					{
						if (inputList.size() > 0 && ItemStack.areItemsEqual(entity.getItem(), inputList.get(0)))
						{
							entity.getItem().shrink(1);
							if (entity.getItem().isEmpty()) world.removeEntity(entity);
							inputList.remove(0);
							continue;
						}
						if (oredictList.size() > 0 && OreDictionary.getOres(oredictList.get(0), false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, entity.getItem())))
						{
							entity.getItem().shrink(1);
							if (entity.getItem().isEmpty()) world.removeEntity(entity);
							oredictList.remove(0);
							continue;
						}
					}
				}

				EntityItem output = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, outputItem);
				output.motionX = 0;
				output.motionY = 0;
				output.motionZ = 0;
				output.forceSpawn = true;
				world.spawnEntity(output);

				if (explode)
				{
					PacketHandler.NETWORK.sendToAllAround(new PacketExplode(output.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true), new NetworkRegistry.TargetPoint(world.provider.getDimension(), output.posX, output.posY, output.posZ, 256));
					Utils.boom(world, output);
				}

				if (!silent) world.playSound(null, output.posX, output.posY, output.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
			}
		}, identifier, duration);
	}

	private ManaCrafterBuilder buildManaCrafter(String identifier, IBlockState outputBlock, String inputOredict, List<ItemStack> extraInputs, List<String> extraInputOredicts, int duration, int radius, boolean consume, boolean explode, boolean silent)
	{
		return new ManaCrafterBuilder(new ManaCrafterPredicate()
		{
			@Override
			public boolean check(World world, BlockPos pos, List<EntityItem> items)
			{
				if (radius > 0) for (int i = -radius; i <= radius; i++)
					for (int j = -radius; j <= radius; j++)
						if (world.getBlockState(pos.add(i, 0, j)) != ModBlocks.FLUID_MANA.getDefaultState()) return false;

				boolean foundInput = false;
				List<ItemStack> list = items.stream().map(entity -> entity.getItem()).collect(Collectors.toList());
				List<String> oredictList = new LinkedList<>();
				oredictList.addAll(extraInputOredicts);
				List<ItemStack> inputList = new LinkedList<>();
				inputList.addAll(extraInputs);
				for (ItemStack item : list)
				{
					if (foundInput && inputList.size() <= 0 && oredictList.size() <= 0) return true;
					if (!foundInput && OreDictionary.getOres(inputOredict, false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, item)))
					{
						foundInput = true;
						continue;
					}
					if (inputList.size() > 0 && ItemStack.areItemsEqual(item, inputList.get(0)))
					{
						inputList.remove(0);
						continue;
					}
					if (oredictList.size() > 0 && OreDictionary.getOres(oredictList.get(0), false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, item)))
					{
						oredictList.remove(0);
						continue;
					}
				}
				return foundInput && inputList.size() <= 0 && oredictList.size() <= 0;
			}
		}, new ManaCrafterConsumer()
		{
			@Override
			public void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration)
			{
				EntityItem entityItem = items.stream().filter(entity -> OreDictionary.getOres(inputOredict, false).stream().anyMatch(ore -> ItemStack.areItemsEqual(entity.getItem(), ore))).findFirst().orElse(null);
				if (world.isRemote) LibParticles.CRAFTING_ALTAR_IDLE(world, entityItem.getPositionVector());
				if (!silent && currentDuration % 40 == 0) world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
			}
		}, new ManaCrafterConsumer()
		{
			@Override
			public void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration)
			{
				if (consume) for (int i = -radius; i <= radius; i++)
					for (int j = -radius; j <= radius; j++)
						world.setBlockToAir(pos.add(i, 0, j));

				List<String> oredictList = new LinkedList<>();
				oredictList.addAll(extraInputOredicts);
				List<ItemStack> inputList = new LinkedList<>();
				inputList.addAll(extraInputs);
				oredictList.add(inputOredict);

				while ((inputList.size() > 0 || oredictList.size() > 0) && items.size() > 0)
				{
					for (EntityItem entity : items)
					{
						if (inputList.size() > 0 && ItemStack.areItemsEqual(entity.getItem(), inputList.get(0)))
						{
							entity.getItem().shrink(1);
							if (entity.getItem().isEmpty()) world.removeEntity(entity);
							inputList.remove(0);
							continue;
						}
						if (oredictList.size() > 0 && OreDictionary.getOres(oredictList.get(0), false).stream().anyMatch(ore -> ItemStack.areItemsEqual(ore, entity.getItem())))
						{
							entity.getItem().shrink(1);
							if (entity.getItem().isEmpty()) world.removeEntity(entity);
							oredictList.remove(0);
							continue;
						}
					}
				}

				world.setBlockState(pos, outputBlock);
				Vec3d output = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

				if (explode)
				{
					PacketHandler.NETWORK.sendToAllAround(new PacketExplode(output, Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true), new NetworkRegistry.TargetPoint(world.provider.getDimension(), output.x, output.y, output.z, 256));
					Utils.boom(world, output);
				}

				if (!silent) world.playSound(null, output.x, output.y, output.z, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
			}
		}, identifier, duration);
	}
	
	public class ManaCrafterBuilder
	{
		ManaCrafterPredicate isValid;
		ManaCrafterConsumer tick;
		ManaCrafterConsumer finish;
		String identifier;
		int duration;
		
		public ManaCrafterBuilder(ManaCrafterPredicate isValid, ManaCrafterConsumer tick, ManaCrafterConsumer finish, String identifier, int duration)
		{
			this.isValid = isValid;
			this.tick = tick;
			this.finish = finish;
			this.identifier = identifier;
			this.duration = duration;
		}
		
		public ManaCrafter build()
		{
			return new ManaCrafter(identifier, duration)
					{
						@Override
						public boolean isValid(World world, BlockPos pos, List<EntityItem> items)
						{
							return isValid.check(world, pos, items);
						}
						
						@Override
						public void tick(World world, BlockPos pos, List<EntityItem> items)
						{
							super.tick(world, pos, items);
							tick.consume(world, pos, items, currentDuration);
						}

						@Override
						public void finish(World world, BlockPos pos, List<EntityItem> items)
						{
							finish.consume(world, pos, items, currentDuration);
						}
					};
		}
	}
	
	@FunctionalInterface
	private interface ManaCrafterPredicate
	{
		public boolean check(World world, BlockPos pos, List<EntityItem> items);
	}
	
	@FunctionalInterface
	private interface ManaCrafterConsumer
	{
		public void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration);
	}
}
