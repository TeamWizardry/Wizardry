package com.teamwizardry.wizardry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.teamwizardry.wizardry.api.spell.Pattern;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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
	    eventBus.addListener(this::registerPatterns);
	}
	
	private void registerRegistries(RegistryEvent.NewRegistry event)
	{
	    new RegistryBuilder<Pattern>().setType(Pattern.class).disableSaving().create();
	}
	
	private void registerPatterns(RegistryEvent.Register<Pattern> event)
	{
	    event.getRegistry().register(new Pattern() {
	        @Override public void run() {}
	        @Override public void affectEntity(Entity entity) {}
	        @Override public void affectBlock(BlockPos pos) {}
	    }.setRegistryName(MODID, "burn"));
	}
	
	public void init(final FMLCommonSetupEvent event)
	{
		LOGGER.info("Initializing!");
		/* Let's leave this until we need to do things in-game */
//		DeferredWorkQueue.runLater(() -> {
//		    IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
//		    ModuleLoader.loadModules(resourceManager);
//		});
	}
}
