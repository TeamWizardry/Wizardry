package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.librarianlib.LibrarianLib;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Config;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.client.gui.GuiHandler;
import com.teamwizardry.wizardry.common.achievement.AchievementEvents;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.fluid.Fluids;
import com.teamwizardry.wizardry.common.network.PacketParticleAmbientFizz;
import com.teamwizardry.wizardry.common.network.PacketParticleMagicDot;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import com.teamwizardry.wizardry.common.network.WizardryPacketHandler;
import com.teamwizardry.wizardry.common.world.GenHandler;
import com.teamwizardry.wizardry.common.world.underworld.WorldProviderUnderWorld;
import com.teamwizardry.wizardry.init.*;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		WizardryPacketHandler.registerMessages();

		Config.initConfig(event.getSuggestedConfigurationFile());

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

		Wizardry.underWorld = DimensionType.register("underworld", "_dim", Config.underworld_id, WorldProviderUnderWorld.class, false);
		DimensionManager.registerDimension(Config.underworld_id, Wizardry.underWorld);

		MinecraftForge.EVENT_BUS.register(new WorldProviderUnderWorld());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new AchievementEvents());
		MinecraftForge.EVENT_BUS.register(new ModCapabilities());

		PacketHandler.register(PacketParticleMagicDot.class, Side.CLIENT);
		PacketHandler.register(PacketParticleAmbientFizz.class, Side.CLIENT);
		PacketHandler.register(PacketRenderSpell.class, Side.CLIENT);
	}

	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new GenHandler(), 0);
		ModRecipes.initCrafting();

		FMLInterModComms.sendMessage(LibrarianLib.MODID, "unsafe", "librarianliblate");
	}

	public void postInit(FMLPostInitializationEvent event) {
		ModuleRegistry.INSTANCE.getClass();
	}

	public boolean isClient() {
		return false;
	}

	public void openGUI(Object gui) {

	}
}
