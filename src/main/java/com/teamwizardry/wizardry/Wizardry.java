package com.teamwizardry.wizardry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamwizardry.wizardry.api.spell.PatternRegistry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Wizardry.MODID)
public class Wizardry
{
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static final String MODID = "wizardry";
	
	public Wizardry()
	{
	    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
	    
		PatternRegistry.registerPatterns();
	}
	
	public void init(final FMLCommonSetupEvent event)
	{
		LOGGER.info("Initializing!");
//		ModuleLoader.loadModules();
	}
}
