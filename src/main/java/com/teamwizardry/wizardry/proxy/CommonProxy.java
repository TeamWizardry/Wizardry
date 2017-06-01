package com.teamwizardry.wizardry.proxy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.config.EasyConfigHandler;
import com.teamwizardry.librarianlib.features.kotlin.JsonMaker;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.client.gui.GuiHandler;
import com.teamwizardry.wizardry.common.achievement.AchievementEvents;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.core.EventHandler;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.fluid.Fluids;
import com.teamwizardry.wizardry.common.network.*;
import com.teamwizardry.wizardry.common.world.GenHandler;
import com.teamwizardry.wizardry.common.world.underworld.WorldProviderUnderWorld;
import com.teamwizardry.wizardry.init.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.*;

public class CommonProxy {

	public static File directory;

	public void preInit(FMLPreInitializationEvent event) {

		int tempFix = 42;
		directory = event.getModConfigurationDirectory();

		new ModTab();
		ModSounds.init();
		ModItems.init();
		ModBlocks.init();
		Achievements.init();
		Fluids.preInit();
		ModEntities.init();
		ModPotions.init();
		ModCapabilities.preInit();
		ModBiomes.init();

		WizardryPacketHandler.registerMessages();
		NetworkRegistry.INSTANCE.registerGuiHandler(Wizardry.instance, new GuiHandler());

		ModStructures.INSTANCE.getClass();

		Wizardry.underWorld = DimensionType.register("underworld", "_dim", tempFix, WorldProviderUnderWorld.class, false);
		DimensionManager.registerDimension(tempFix, Wizardry.underWorld);

		MinecraftForge.EVENT_BUS.register(new WorldProviderUnderWorld());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new AchievementEvents());
		MinecraftForge.EVENT_BUS.register(new ModCapabilities());
		MinecraftForge.EVENT_BUS.register(this);
		SpellTicker.INSTANCE.getClass();

		PacketHandler.register(PacketRenderSpell.class, Side.CLIENT);
		PacketHandler.register(PacketExplode.class, Side.CLIENT);
		PacketHandler.register(PacketSyncModuleRegistry.class, Side.CLIENT);
		PacketHandler.register(PacketFreezePlayer.class, Side.CLIENT);
		PacketHandler.register(PacketSendSpellToBook.class, Side.SERVER);
		PacketHandler.register(PacketRenderLightningBolt.class, Side.CLIENT);
	}

	public void init(FMLInitializationEvent event) {
		EasyConfigHandler.init();
		GameRegistry.registerWorldGenerator(new GenHandler(), 0);
		ModRecipes.initCrafting();
	}

	public void postInit(FMLPostInitializationEvent event) {
		ModuleRegistry.INSTANCE.getClass();
	}

	@SubscribeEvent
	public void worldJoin(EntityJoinWorldEvent event) {
		if (!(event.getEntity() instanceof EntityPlayer)) return;

		File config = new File(directory.getPath() + "/" + Wizardry.MODID, "module_registry.json");

		if (!config.exists())
			if (!createModuleRegistryFile(config.getParentFile())) {
				Wizardry.logger.error("SOMETHING WENT WRONG! Could not create module_registry.json");
			}

		if (event.getWorld().isRemote) {

			JsonParser parser = new JsonParser();
			try {
				JsonElement element = parser.parse(new FileReader(config));
				JsonObject obj = element.getAsJsonObject();

				ModuleRegistry.INSTANCE.processModules(obj);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		} else PacketHandler.NETWORK.sendTo(new PacketSyncModuleRegistry(config), (EntityPlayerMP) event.getEntity());
	}

	private boolean createModuleRegistryFile(File directory) {
		if (!directory.exists()) {
			Wizardry.logger.info(directory.getName() + " not found. Creating directory...");
			if (!directory.mkdirs()) {
				Wizardry.logger.error("SOMETHING WENT WRONG! Could not create config directory " + directory.getName());
				return false;
			}
			Wizardry.logger.info(directory.getName() + " has been created successfully!");
		}

		File registryFile = new File(directory, "module_registry.json");
		try {
			if (!registryFile.exists()) {
				Wizardry.logger.info(registryFile.getName() + " file not found. Creating file...");
				if (!registryFile.createNewFile()) {
					Wizardry.logger.fatal("SOMETHING WENT WRONG! Could not create config file " + registryFile.getName());
					return false;
				}

				InputStream stream = LibrarianLib.PROXY.getResource(Wizardry.MODID, "module_registry.json");

				if (stream == null) {
					Wizardry.logger.fatal("SOMETHING WENT WRONG! Module Registry file does not exist in mod jar!");
					return false;
				}

				InputStreamReader reader = new InputStreamReader(stream);
				JsonElement element = new JsonParser().parse(reader);

				if (!element.isJsonObject()) {
					Wizardry.logger.fatal("SOMETHING WENT WRONG! Module Registry's json is not a JsonObject");
					return false;
				}

				JsonObject obj = element.getAsJsonObject();

				FileWriter writer = new FileWriter(registryFile);
				writer.write(JsonMaker.serialize(obj));
				writer.flush();
				writer.close();
				Wizardry.logger.info(registryFile.getName() + " file has been created successfully!");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
