package com.teamwizardry.wizardry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.common.init.PatternInit;
import com.teamwizardry.wizardry.common.spell.ModuleLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(Wizardry.MODID)
public class Wizardry
{
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static final String MODID = "wizardry";
	
	public Wizardry()
	{
	    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
	    eventBus.addListener(this::init);
	    eventBus.addListener(this::registerRegistries);
	    eventBus.addGenericListener(Pattern.class, this::registerPatterns);
	}
	
	private void registerRegistries(RegistryEvent.NewRegistry event)
	{
	    new RegistryBuilder<Pattern>().setType(Pattern.class)
	                                  .setName(new ResourceLocation(MODID, "pattern"))
	                                  .disableSaving()
	                                  .create();
	}
	
	private void registerPatterns(RegistryEvent.Register<Pattern> event)
	{
	    PatternInit.init(event.getRegistry());
	}
	
	public void init(final FMLCommonSetupEvent event)
	{
		LOGGER.info("Initializing!");
		/* Let's leave this until we need to do things in-game */
		DeferredWorkQueue.runLater(() -> {
		    LOGGER.info("Loading Modules");
		    IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		    ModuleLoader.loadModules(resourceManager);
		    LOGGER.info("Finished loading Modules");
		});
	}
}
