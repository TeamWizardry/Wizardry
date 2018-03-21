package com.teamwizardry.wizardry.crafting.mana;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class ManaRecipes {
	public static final ManaRecipes INSTANCE = new ManaRecipes();

	public static final HashMap<String, FluidRecipeLoader.FluidCrafter> RECIPE_REGISTRY = new HashMap<>();
	public static final HashMultimap<Ingredient, FluidRecipeLoader.FluidCrafter> RECIPES = HashMultimap.create();

	public static final String EXPLODABLE = "explodable";

	public void loadRecipes(File directory) {
		FluidRecipeLoader.INSTANCE.setDirectory(directory);
		FluidRecipeLoader.INSTANCE.processRecipes(RECIPE_REGISTRY, RECIPES);
	}

	public static String[] getResourceListing(String mod, String path) {
		List<String> all = Lists.newArrayList();
		if (CraftingHelper.findFiles(Loader.instance().getIndexedModList().get(mod), "assets/" + mod + "/" + path, null,
				(root, full) -> all.add(root.relativize(full).toString()), false, false))
			return all.toArray(new String[0]);
		return new String[0];
	}

	public void copyMissingRecipes(File directory) {
		for (String recipeName : getResourceListing(Wizardry.MODID, "fluid_recipes")) {
			File file = new File(directory, recipeName);
			if (file.exists()) continue;

			InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "fluid_recipes/" + recipeName);
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

	public static class ExplodableCrafter extends FluidCraftInstance {
		public ExplodableCrafter() {
			super(EXPLODABLE, 200, ModFluids.MANA.getActual());
		}

		@Override
		public boolean isValid(World world, BlockPos pos, List<EntityItem> items) {
			Block at = world.getBlockState(pos).getBlock();
			return at == fluid.getBlock() && items.stream().map(entity -> entity.getItem().getItem()).anyMatch(item -> item instanceof IExplodable);
		}

		@Override
		public void tick(World world, BlockPos pos, List<EntityItem> items) {
			super.tick(world, pos, items);
			EntityItem item = items.stream().filter(entity -> entity.getItem().getItem() instanceof IExplodable).findFirst().orElse(null);
			if (item != null)
				if (currentDuration % 40 == 0)
					world.playSound(null, item.posX, item.posY, item.posZ, ModSounds.BUBBLING, SoundCategory.AMBIENT, 0.7F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
		}

		@Override
		public void finish(World world, BlockPos pos, List<EntityItem> items) {
			EntityItem item = items.stream().filter(entity -> entity.getItem().getItem() instanceof IExplodable).findFirst().orElse(null);
			if (item != null) {
				((IExplodable) item.getItem().getItem()).explode(item);
				world.setBlockToAir(pos);
				world.removeEntity(item);
				world.playSound(null, item.posX, item.posY, item.posZ, ModSounds.GLASS_BREAK, SoundCategory.AMBIENT, 0.5F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
			}
		}
	}
}
