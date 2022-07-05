package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.librarianlib.features.gui.provided.book.helper.PageTypes;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.arena.ArenaManager;
import com.teamwizardry.wizardry.api.capability.chunk.WizardryChunkCapability;
import com.teamwizardry.wizardry.api.capability.world.WizardryWorldCapability;
import com.teamwizardry.wizardry.api.plugin.PluginContext;
import com.teamwizardry.wizardry.api.plugin.WizardryPlugin;
import com.teamwizardry.wizardry.api.spell.ProcessData;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.client.gui.GuiHandler;
import com.teamwizardry.wizardry.client.gui.book.PageWizardryStructure;
import com.teamwizardry.wizardry.common.advancement.AchievementEvents;
import com.teamwizardry.wizardry.common.core.EventHandler;
import com.teamwizardry.wizardry.common.core.version.manifest.ManifestHandler;
import com.teamwizardry.wizardry.common.core.version.manifest.ManifestUpgrader;
import com.teamwizardry.wizardry.common.item.ItemBook;
import com.teamwizardry.wizardry.common.module.effects.ModuleEffectLeap;
import com.teamwizardry.wizardry.common.module.effects.ModuleEffectTimeSlow;
import com.teamwizardry.wizardry.common.network.*;
import com.teamwizardry.wizardry.common.world.underworld.WorldProviderUnderWorld;
import com.teamwizardry.wizardry.crafting.burnable.FireRecipes;
import com.teamwizardry.wizardry.crafting.mana.ManaRecipes;
import com.teamwizardry.wizardry.init.*;
import com.teamwizardry.wizardry.init.plugin.PluginLoaderContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.util.ServiceLoader;

public class CommonProxy {

	private File directory;
	public static final ResourceLocation UNDERWORLD_BOAT_CHEST = new ResourceLocation(Wizardry.MODID, "underworld_boat_chest");

	public File getWizardryDirectory() {
		return directory;
	}

	private final PluginContext context = new PluginLoaderContext();

	public void setItemStackHandHandler(EnumHand hand, ItemStack stack) {
	}

	public void preInit(FMLPreInitializationEvent event) {
		directory = new File(event.getModConfigurationDirectory(), Wizardry.MODID);
		if (!directory.exists()) if (!directory.mkdirs())
			Wizardry.LOGGER.fatal("    > SOMETHING WENT WRONG! Could not create config folder!!");

		new SpellData.DefaultKeys();

		ManifestUpgrader maniUpgrader = ManifestHandler.INSTANCE.startUpgrade(directory);
		maniUpgrader.changeCategoryName("modules", "wizmodules");
		maniUpgrader.finalizeUpgrade();

		ManifestHandler.INSTANCE.loadNewInternalManifest("wizmodules", "fluid_recipes", "fire_recipes");
		ManifestHandler.INSTANCE.loadExternalManifest(directory);
		ManifestHandler.INSTANCE.processComparisons(directory, "wizmodules", "fluid_recipes", "fire_recipes");

		new ModTab();
		ModBlocks.init();
		ModItems.init();
		ModSounds.init();
		ModPotions.init();
		ModEntities.init();
		ModCapabilities.preInit();

		NetworkRegistry.INSTANCE.registerGuiHandler(Wizardry.instance, new GuiHandler());

		// Loot tables
		LootTableList.register(UNDERWORLD_BOAT_CHEST);

		Wizardry.underWorld = DimensionType.register("underworld", "_dim", ConfigValues.underworldID, WorldProviderUnderWorld.class, false);
//		Wizardry.torikki = DimensionType.register("torikki", "_dim", ConfigValues.torikkiID, WorldProviderTorikki.class, false);
		DimensionManager.registerDimension(ConfigValues.underworldID, Wizardry.underWorld);
//		DimensionManager.registerDimension(ConfigValues.torikkiID, Wizardry.torikki);

		MinecraftForge.EVENT_BUS.register(ArenaManager.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new WorldProviderUnderWorld());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new AchievementEvents());
		MinecraftForge.EVENT_BUS.register(new ModuleEffectTimeSlow());
		MinecraftForge.EVENT_BUS.register(new ModuleEffectLeap());
		MinecraftForge.EVENT_BUS.register(ModBiomes.BIOME_UNDERWORLD);
//		MinecraftForge.EVENT_BUS.register(ModBiomes.BIOME_TORIKKI);
//		MinecraftForge.EVENT_BUS.register(ModBiomes.BIOME_TORIKKISEA);
		MinecraftForge.EVENT_BUS.register(this);

		WizardryWorldCapability.init();
		WizardryChunkCapability.init();

		PacketHandler.register(PacketSendSpellToBook.class, Side.SERVER);
		PacketHandler.register(PacketRenderSpell.class, Side.CLIENT);
		PacketHandler.register(PacketExplode.class, Side.CLIENT);
		PacketHandler.register(PacketFreezePlayer.class, Side.CLIENT);
		PacketHandler.register(PacketRenderLightningBolt.class, Side.CLIENT);
		PacketHandler.register(PacketSyncCooldown.class, Side.CLIENT);
		PacketHandler.register(PacketDevilDustFizzle.class, Side.CLIENT);


		PageTypes.INSTANCE.registerPageProvider("wizardry_structure", PageWizardryStructure::new);
		ItemBook.BOOK = new Book("book");

		Wizardry.LOGGER.info("Initializing fairy task plugins...");
		for (final WizardryPlugin plugin : ServiceLoader.load(WizardryPlugin.class)) {
			Wizardry.LOGGER.info("Initializing plugin {}", plugin.getClass().getName());
			plugin.onInit(context);
		}
		Wizardry.LOGGER.info("Initialization complete!");
	}

	public void init(FMLInitializationEvent event) {
		ModStructures.init();

		manaRecipeLoading:
		{
			File recipeDirectory = new File(directory, "fluid_recipes");
			if (!recipeDirectory.exists()) {
				if (!recipeDirectory.mkdirs()) {
					Wizardry.LOGGER.error("    > SOMETHING WENT WRONG! Could not create directory " + recipeDirectory.getPath());
					break manaRecipeLoading;
				}
			}
			if (ConfigValues.useInternalValues)
				ManaRecipes.INSTANCE.copyAllRecipes(recipeDirectory);
			ManaRecipes.INSTANCE.loadRecipes(recipeDirectory);
		}
		fireRecipeLoading:
		{
			File recipeDirectory = new File(directory, "fire_recipes");
			if (!recipeDirectory.exists()) {
				if (!recipeDirectory.mkdirs()) {
					Wizardry.LOGGER.error("    > SOMETHING WENT WRONG! Could not create directory " + recipeDirectory.getPath());
					break fireRecipeLoading;
				}
			}
			if (ConfigValues.useInternalValues)
				FireRecipes.INSTANCE.copyAllRecipes(recipeDirectory);
			FireRecipes.INSTANCE.loadRecipes(recipeDirectory);
		}

		moduleLoading:
		{
			File moduleDirectory = new File(directory, "wizmodules");
			if (!moduleDirectory.exists())
				if (!moduleDirectory.mkdirs()) {
					Wizardry.LOGGER.error("    > SOMETHING WENT WRONG! Could not create directory " + moduleDirectory.getPath());
					break moduleLoading;
				}

			ModuleRegistry.INSTANCE.loadUnprocessedModules();
			ModuleRegistry.INSTANCE.loadOverrideDefaults();
			if (ConfigValues.useInternalValues)
				ModuleRegistry.INSTANCE.copyAllModules(moduleDirectory);
			ModuleRegistry.INSTANCE.loadModules(moduleDirectory);
		}

		ProcessData.INSTANCE.registerAnnotatedDataTypes();
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

}
