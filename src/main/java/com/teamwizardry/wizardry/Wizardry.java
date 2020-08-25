package com.teamwizardry.wizardry;

import com.teamwizardry.librarianlib.foundation.BaseMod;
import com.teamwizardry.wizardry.api.capability.mana.IManaCapability;
import com.teamwizardry.wizardry.api.capability.mana.ManaCapabilityImpl;
import com.teamwizardry.wizardry.api.capability.mana.ManaStorage;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.common.init.ModBlocks;
import com.teamwizardry.wizardry.common.init.ModItems;
import com.teamwizardry.wizardry.common.init.PatternInit;
import com.teamwizardry.wizardry.common.spell.loading.ModuleLoader;
import com.teamwizardry.wizardry.proxy.ClientProxy;
import com.teamwizardry.wizardry.proxy.IProxy;
import com.teamwizardry.wizardry.proxy.ServerProxy;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Wizardry.MODID)
public class Wizardry extends BaseMod {
	public static final String MODID = "wizardry";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static IProxy PROXY;
	public static Wizardry INSTANCE;

	public Wizardry() {
		INSTANCE = this;
		PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::init);
		eventBus.addListener(this::registerRegistries);
		MinecraftForge.EVENT_BUS.addListener(this::serverStartingEvent);
		eventBus.addGenericListener(Pattern.class, this::registerPatterns);

		// Initialize Items
		ModItems.initializeItems(getRegistrationManager());
		ModItems.initializeItemGroup();

		// Initialize Blocks
		ModBlocks.registerBlocks(getRegistrationManager());

		PROXY.registerHandlers();
	}

	public static ResourceLocation location(String path) {
		return new ResourceLocation(MODID, path);
	}

	private void registerRegistries(RegistryEvent.NewRegistry event) {
		new RegistryBuilder<Pattern>().setType(Pattern.class)
				.setName(new ResourceLocation(MODID, "pattern"))
				.disableSaving()
				.create();
	}

	private void registerPatterns(RegistryEvent.Register<Pattern> event) {
		PatternInit.init(event.getRegistry());
	}

	public void serverStartingEvent(FMLServerAboutToStartEvent event) {
		IReloadableResourceManager manager = event.getServer().getResourceManager();
		manager.addReloadListener((ISelectiveResourceReloadListener) (listener, predicate) -> ModuleLoader.loadModules(manager));

		CapabilityManager.INSTANCE.register(IManaCapability.class, new ManaStorage(), () -> new ManaCapabilityImpl(0, 1000, 1000, 1000));
	}

	public void init(final FMLCommonSetupEvent event) {
		LOGGER.info("Initializing!");
	}
}
