package com.teamwizardry.wizardry.crafting.mana;

import com.google.common.collect.HashMultimap;
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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ManaRecipes {
	public static final ManaRecipes INSTANCE = new ManaRecipes();

	public static final HashMap<String, FluidRecipeLoader.FluidCrafter> RECIPE_REGISTRY = new HashMap<>();
	public static final HashMultimap<Ingredient, FluidRecipeLoader.FluidCrafter> RECIPES = HashMultimap.create();

	public static final String EXPLODABLE = "explodable";

	public void loadRecipes(File directory) {
		FluidRecipeLoader.INSTANCE.setDirectory(directory);
		FluidRecipeLoader.INSTANCE.processRecipes(RECIPE_REGISTRY, RECIPES);
	}

	// Todo replace in liblib 4.9 with CommonUtilMethods.getAllResources
	public static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
		URL dirURL = clazz.getClassLoader().getResource(path);
		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			/* A file path: easy enough */
			return new File(dirURL.toURI()).list();
		}

		if (dirURL == null) {
			/*
			 * In case of a jar file, we can't actually find a directory.
			 * Have to assume the same jar as clazz.
			 */
			String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")) {
			/* A JAR path */
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); // strip out only the JAR file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
			Set<String> result = new HashSet<>(); // avoid duplicates in case it is a subdirectory
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(path)) { // filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					result.add(entry);
				}
			}
			return result.toArray(new String[result.size()]);
		}

		throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
	}

	public void copyMissingRecipes(File directory) {
		try {
			for (String recipeName : getResourceListing(ManaRecipes.class, "assets/" + Wizardry.MODID + "/fluid_recipes/")) {
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
		} catch (IOException | URISyntaxException e) {
			Wizardry.logger.fatal("    > SOMETHING WENT WRONG! Could not read recipes from mod jar! Report this to the devs on Github!");
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
