package com.teamwizardry.wizardry.crafting.mana;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.crafting.mana.ManaRecipeLoader.ManaCrafterBuilder;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class ManaRecipes {
	public static final ManaRecipes INSTANCE = new ManaRecipes();

	public static final HashMap<String, ManaCrafterBuilder> RECIPE_REGISTRY = new HashMap<>();
	public static final HashMultimap<Ingredient, ManaCrafterBuilder> RECIPES = HashMultimap.create();

	public static final String CODEX = "codex";
	public static final String NACRE = "nacre";
	public static final String EXPLODABLE = "explodable";
	public static final String MANA_BATTERY = "mana_battery";

	private static final String[] INTERNAL_RECIPE_NAMES = {CODEX.toLowerCase(),
			NACRE.toLowerCase(),
			MANA_BATTERY.toLowerCase(),
			"wisdom_log",
			"wisdom_plank",
			"wisdom_slab",
			"wisdom_stairs",
			"wisdom_stick",
			"temp_real_halo"};

	public void loadRecipes(File directory) {
		ManaRecipeLoader.INSTANCE.setDirectory(directory);
		ManaRecipeLoader.INSTANCE.processRecipes(RECIPE_REGISTRY, RECIPES);
	}

	public void copyMissingRecipes(File directory) {
		for (String recipeName : INTERNAL_RECIPE_NAMES) {
			File file = new File(directory, recipeName + ".json");
			if (file.exists()) continue;

			InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "mana_recipes/" + recipeName + ".json");
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

	public class ExplodableCrafter extends ManaCrafter {
		public ExplodableCrafter() {
			super(EXPLODABLE, 200);
		}

		@Override
		public boolean isValid(World world, BlockPos pos, List<EntityItem> items) {
			return items.stream().map(entity -> entity.getItem().getItem()).anyMatch(item -> item instanceof IExplodable);
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
