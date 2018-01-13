package com.teamwizardry.wizardry.crafting.mana;

import com.google.common.collect.Multimap;
import com.google.gson.*;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class ManaRecipeLoader {
	public static final ManaRecipeLoader INSTANCE = new ManaRecipeLoader();

	private File directory;

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	@SuppressWarnings("deprecation")
	public void processRecipes(Map<String, ManaCrafterBuilder> recipeRegistry, Multimap<Ingredient, ManaCrafterBuilder> recipes) {
		Wizardry.logger.info("<<========================================================================>>");
		Wizardry.logger.info("> Starting mana recipe loading.");

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

				List<Ingredient> extraInputs = new LinkedList<>();
				int duration = 100;
				int radius = 0;
				boolean consume = false;
				boolean explode = false;
				boolean bubbling = true;
				boolean harp = true;

				if (recipeRegistry.containsKey(file.getPath())) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " already exists in the recipe map. Ignoring file...: " + element.toString());
					continue;
				}

				if (!fileObject.has("type")) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT specify a recipe output type. Ignoring file...: " + element.toString());
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

				if (!fileObject.has("input")) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide an initial input item. Ignoring file...: " + element.toString());
					continue;
				}

				JsonElement inputObject = fileObject.get("input");
				Ingredient inputItem = CraftingHelper.getIngredient(inputObject, context);

				if (inputItem == Ingredient.EMPTY) {
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid input item. Ignoring file...: " + element.toString());
					continue;
				}

				if (fileObject.has("extraInputs")) {
					if (!fileObject.get("extraInputs").isJsonArray()) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " has extra inputs NOT in a JsonArray format. Ignoring file...: " + element.toString());
						continue;
					}
					JsonArray extraInputArray = fileObject.get("extraInputs").getAsJsonArray();
					for (JsonElement extraInput : extraInputArray) {
						Ingredient ingredient = CraftingHelper.getIngredient(extraInput, context);
						if (ingredient == Ingredient.EMPTY) {
							Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid extra input item. Ignoring file...: " + element.toString());
							continue fileLoop;
						}
						extraInputs.add(ingredient);
					}
				}

				if (fileObject.has("duration")) {
					if (!fileObject.get("duration").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("duration").isNumber()) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give duration as a number. Ignoring file...:" + element.toString());
						continue;
					}
					duration = fileObject.get("duration").getAsInt();
				}

				if (fileObject.has("radius")) {
					if (!fileObject.get("radius").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("radius").isNumber()) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give radius as a number. Ignoring file...: " + element.toString());
						continue;
					}
					radius = fileObject.get("radius").getAsInt();
				}

				if (fileObject.has("consume")) {
					if (!fileObject.get("consume").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("consume").isBoolean()) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give consume as a boolean. Ignoring file...: " + element.toString());
						continue;
					}
					consume = fileObject.get("consume").getAsBoolean();
				}

				if (fileObject.has("explode")) {
					if (!fileObject.get("explode").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("explode").isBoolean()) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give explode as a boolean. Ignoring file...: " + element.toString());
						continue;
					}
					explode = fileObject.get("explode").getAsBoolean();
				}

				if (fileObject.has("harp")) {
					if (!fileObject.get("harp").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("harp").isBoolean()) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give harp as a boolean. Ignoring file...: " + element.toString());
						continue;
					}
					harp = fileObject.get("harp").getAsBoolean();
				}

				if (fileObject.has("bubbling")) {
					if (!fileObject.get("bubbling").isJsonPrimitive() || !fileObject.getAsJsonPrimitive("bubbling").isBoolean()) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT give bubbling as a boolean. Ignoring file...: " + element.toString());
						continue;
					}
					bubbling = fileObject.get("bubbling").getAsBoolean();
				}

				String type = fileObject.get("type").getAsString();
				JsonObject output = fileObject.get("output").getAsJsonObject();

				if (type.equalsIgnoreCase("item")) {
					ItemStack outputItem = CraftingHelper.getItemStack(output, context);

					if (outputItem.isEmpty()) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output item. Ignoring file...: " + element.toString());
						continue;
					}

					ManaCrafterBuilder build = buildManaCrafter(file.getPath(), outputItem, inputItem, extraInputs, duration, radius, consume, explode, bubbling, harp);
					recipeRegistry.put(file.getPath(), build);
					recipes.put(inputItem, build);
				} else if (type.equalsIgnoreCase("block")) {
					IBlockState outputBlock;
					Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(output.get("name").getAsString()));
					if (block == null) {
						Wizardry.logger.error("  > WARNING! " + file.getPath() + " does NOT provide a valid output block. Ignoring file...: " + element.toString());
						continue;
					}
					int meta = 0;
					if (output.has("meta") && output.get("meta").isJsonPrimitive() && output.getAsJsonPrimitive("meta").isNumber())
						meta = output.get("meta").getAsInt();
					outputBlock = block.getStateFromMeta(meta);

					ManaCrafterBuilder build = buildManaCrafter(file.getPath(), outputBlock, inputItem, extraInputs, duration, radius, consume, explode, bubbling, harp);
					recipeRegistry.put(file.getPath(), build);
					recipes.put(inputItem, build);
				} else
					Wizardry.logger.error("  > WARNING! " + file.getPath() + " specifies an invalid recipe output type. Valid recipe types: \"item\" \"block\". Ignoring file...: " + element.toString());
			} catch (JsonParseException jsonException) {
				Wizardry.logger.error("  > WARNING! Skipping " + file.getPath() + " due to error: ", jsonException);
			}
		}
		Wizardry.logger.info("> Finished mana recipe loading.");
		Wizardry.logger.info("<<========================================================================>>");
	}

	private ManaCrafterBuilder buildManaCrafter(String identifier, ItemStack outputItem, Ingredient input, List<Ingredient> extraInputs, int duration, int radius, boolean consume, boolean explode, boolean bubbling, boolean harp) {
		Ingredient outputIngredient = Ingredient.fromStacks(outputItem);
		return new ManaCrafterBuilder((world, pos, items) -> {
            if (radius > 0) for (int i = -radius; i <= radius; i++)
                for (int j = -radius; j <= radius; j++)
                    if (world.getBlockState(pos.add(i, 0, j)) != ModBlocks.FLUID_MANA.getDefaultState())
                        return false;

            List<ItemStack> list = items.stream().map(entity -> entity.getItem().copy()).collect(Collectors.toList());
            List<Ingredient> inputList = new LinkedList<>();
            inputList.addAll(extraInputs);
            inputList.add(input);
            for (Ingredient itemIn : inputList) {
                boolean foundMatch = false;
                List<ItemStack> toRemove = new LinkedList<>();
                for (ItemStack item : list) {
                    if (itemIn.apply(item) && !outputIngredient.apply(item)) {
                        foundMatch = true;
                        break;
                    }
                }
                if (!foundMatch)
                    return false;
                list.removeAll(toRemove);
                toRemove.clear();
            }
            return true;
        }, (world, pos, items, currentDuration) -> {
            EntityItem entityItem = items.stream().filter(entity -> input.apply(entity.getItem())).findFirst().orElse(null);
            if (entityItem != null) {
                if (world.isRemote) LibParticles.CRAFTING_ALTAR_IDLE(world, entityItem.getPositionVector());
                if (bubbling && currentDuration % 10 == 0)
                    world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
            }
        }, (world, pos, items, currentDuration) -> {
            if (consume) for (int i = -radius; i <= radius; i++)
                for (int j = -radius; j <= radius; j++)
                    world.setBlockToAir(pos.add(i, 0, j));

            List<Ingredient> inputList = new LinkedList<>();
            inputList.addAll(extraInputs);
            inputList.add(input);

			for (Ingredient itemIn : inputList) {
                for (EntityItem entity : items) {
                    if (itemIn.apply(entity.getItem()) && !outputIngredient.apply(entity.getItem()))  {
                        entity.getItem().shrink(1);
                        if (entity.getItem().isEmpty())
                            entity.setDead();
                    }
                }
            }

            EntityItem output = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, outputItem.copy());
            output.motionX = 0;
            output.motionY = 0;
            output.motionZ = 0;
            output.forceSpawn = true;
            world.spawnEntity(output);

            if (explode) {
                PacketHandler.NETWORK.sendToAllAround(new PacketExplode(output.getPositionVector(), Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true), new NetworkRegistry.TargetPoint(world.provider.getDimension(), output.posX, output.posY, output.posZ, 256));
                PosUtils.boom(world, output.getPositionVector(), output, 3, false);
            }

            if (harp)
                world.playSound(null, output.posX, output.posY, output.posZ, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
        }, identifier, duration);
	}

	private ManaCrafterBuilder buildManaCrafter(String identifier, IBlockState outputBlock, Ingredient input, List<Ingredient> extraInputs, int duration, int radius, boolean consume, boolean explode, boolean bubbling, boolean harp) {
		return new ManaCrafterBuilder((world, pos, items) -> {
            if (radius > 0) for (int i = -radius; i <= radius; i++)
                for (int j = -radius; j <= radius; j++)
                    if (world.getBlockState(pos.add(i, 0, j)) != ModBlocks.FLUID_MANA.getDefaultState())
                        return false;

            List<ItemStack> list = items.stream().map(entity -> entity.getItem().copy()).collect(Collectors.toList());
			List<Ingredient> inputList = new LinkedList<>();
			inputList.addAll(extraInputs);
			inputList.add(input);
			for (Ingredient itemIn : inputList) {
				boolean foundMatch = false;
				List<ItemStack> toRemove = new LinkedList<>();
				for (ItemStack item : list) {
					if (itemIn.apply(item)) {
						foundMatch = true;
						break;
					}
				}
				if (!foundMatch)
					return false;
				list.removeAll(toRemove);
				toRemove.clear();
			}
            return true;
        }, (world, pos, items, currentDuration) -> {
            EntityItem entityItem = items.stream().filter(entity -> input.apply(entity.getItem())).findFirst().orElse(null);
            if (entityItem != null) {
				if (world.isRemote) LibParticles.CRAFTING_ALTAR_IDLE(world, entityItem.getPositionVector());
				if (bubbling && currentDuration % 10 == 0)
					world.playSound(null, entityItem.posX, entityItem.posY, entityItem.posZ, ModSounds.BUBBLING, SoundCategory.BLOCKS, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
			}
        }, (world, pos, items, currentDuration) -> {
            if (consume) for (int i = -radius; i <= radius; i++)
                for (int j = -radius; j <= radius; j++)
                    world.setBlockToAir(pos.add(i, 0, j));

			List<Ingredient> inputList = new LinkedList<>();
			inputList.addAll(extraInputs);
			inputList.add(input);

			for (Ingredient itemIn : inputList) {
				for (EntityItem entity : items) {
					if (itemIn.apply(entity.getItem())) {
						entity.getItem().shrink(1);
						if (entity.getItem().isEmpty())
							entity.setDead();
					}
				}
			}

            world.setBlockState(pos, outputBlock);
            Vec3d output = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

            if (explode) {
                PacketHandler.NETWORK.sendToAllAround(new PacketExplode(output, Color.CYAN, Color.BLUE, 0.9, 2, 500, 100, 50, true), new NetworkRegistry.TargetPoint(world.provider.getDimension(), output.x, output.y, output.z, 256));
                PosUtils.boom(world, output, null, 3, false);
            }

            if (harp)
                world.playSound(null, output.x, output.y, output.z, ModSounds.HARP1, SoundCategory.BLOCKS, 0.3F, 1.0F);
        }, identifier, duration);
	}

	public static class ManaCrafterBuilder {
		private ManaCrafterPredicate isValid;
		private ManaCrafterConsumer tick;
		private ManaCrafterConsumer finish;
		private String identifier;
		private int duration;

		private ManaCrafterBuilder(ManaCrafterPredicate isValid, ManaCrafterConsumer tick, ManaCrafterConsumer finish, String identifier, int duration) {
			this.isValid = isValid;
			this.tick = tick;
			this.finish = finish;
			this.identifier = identifier;
			this.duration = duration;
		}

		public ManaCrafter build() {
			return new ManaCrafter(identifier, duration) {
				@Override
				public boolean isValid(World world, BlockPos pos, List<EntityItem> items) {
					return isValid.check(world, pos, items);
				}

				@Override
				public void tick(World world, BlockPos pos, List<EntityItem> items) {
					super.tick(world, pos, items);
					tick.consume(world, pos, items, currentDuration);
				}

				@Override
				public void finish(World world, BlockPos pos, List<EntityItem> items) {
					finish.consume(world, pos, items, currentDuration);
				}
			};
		}
	}

	@FunctionalInterface
	private interface ManaCrafterPredicate {
		boolean check(World world, BlockPos pos, List<EntityItem> items);
	}

	@FunctionalInterface
	private interface ManaCrafterConsumer {
		void consume(World world, BlockPos pos, List<EntityItem> items, int currentDuration);
	}
}
