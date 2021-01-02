package com.teamwizardry.wizardry;

import com.teamwizardry.librarianlib.foundation.BaseMod;
import com.teamwizardry.wizardry.api.WizConsts;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.common.init.ModBlocks;
import com.teamwizardry.wizardry.common.init.ModItems;
import com.teamwizardry.wizardry.common.init.PatternInit;
import com.teamwizardry.wizardry.common.packet.CRenderSpellPacket;
import com.teamwizardry.wizardry.common.spell.component.ComponentRegistry;
import com.teamwizardry.wizardry.common.spell.loading.ModifierLoader;
import com.teamwizardry.wizardry.common.spell.loading.ModuleLoader;
import com.teamwizardry.wizardry.proxy.ClientProxy;
import com.teamwizardry.wizardry.proxy.IProxy;
import com.teamwizardry.wizardry.proxy.ServerProxy;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

@Mod(Wizardry.MODID)
public class Wizardry extends BaseMod {
    public static final String MODID = "wizardry";

    public static IProxy PROXY;
    public static Wizardry INSTANCE;

    public Wizardry() {
        INSTANCE = this;
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        
        // Initialize API Constants
        setLoggerBaseName(Wizardry.MODID.toUpperCase().charAt(0) + Wizardry.MODID.substring(1));
        WizConsts.setLogger(this.getLogger());
        WizConsts.setCourier(this.getCourier());

        // Initialize Items
        ModItems.initializeItems(getRegistrationManager());
        ModItems.initializeItemGroup();

        // Initialize Blocks + Tile Entities
        ModBlocks.registerTile(getRegistrationManager());
        ModBlocks.registerBlocks(getRegistrationManager());

        // Register packets
        this.getCourier().register(new CRenderSpellPacket(), NetworkDirection.PLAY_TO_CLIENT);

        PROXY.registerHandlers();
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MODID, path);
    }

    @Override
    protected void createRegistries()
    {
        new RegistryBuilder<Pattern>().setType(Pattern.class)
            .setName(location("pattern"))
            .disableSaving()
            .create();
    }
    
    @SubscribeEvent
    public void registerPatterns(RegistryEvent.Register<Pattern> event) {
        PatternInit.init(event.getRegistry());
    }

    @SubscribeEvent
    public void serverStartingEvent(FMLServerAboutToStartEvent event) {
        IReloadableResourceManager manager = event.getServer().getResourceManager();
        manager.addReloadListener((ISelectiveResourceReloadListener) (listener, predicate) -> {
            ComponentRegistry.loadTargets();
            ModifierLoader.loadModifiers(manager);
            ModuleLoader.loadModules(manager);
        });
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        PROXY.clientSetup();
    }
}
