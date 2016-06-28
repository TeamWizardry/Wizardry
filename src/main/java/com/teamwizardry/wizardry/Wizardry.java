package com.teamwizardry.wizardry;

import com.teamwizardry.wizardry.api.Config;
import com.teamwizardry.wizardry.api.gui.hud.WizardHandler;
import com.teamwizardry.wizardry.client.gui.GuiHandler;
import com.teamwizardry.wizardry.common.event.EventHandler;
import com.teamwizardry.wizardry.common.fluid.Fluids;
import com.teamwizardry.wizardry.common.network.PacketHandler;
import com.teamwizardry.wizardry.common.proxy.CommonProxy;
import com.teamwizardry.wizardry.common.world.GenHandler;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModRecipes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.PacketLoggingHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

/**
 * Created by Saad on 6/9/2016.
 */
@Mod(modid = Wizardry.MODID, version = Wizardry.VERSION, name = Wizardry.MODNAME, useMetadata = true)
public class Wizardry {

    public static final String MODID = "wizardry";
    public static final String MODNAME = "Wizardry";
    public static final String VERSION = "1.0";
    public static final String CLIENT = "com.teamwizardry.wizardry.client.proxy.ClientProxy";
    public static final String SERVER = "com.teamwizardry.wizardry.common.proxy.CommonProxy";
    public static PacketLoggingHandler packetHandler;
    public static Logger logger;
    public static EventBus EVENT_BUS = new EventBus();

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    public static CommonProxy proxy;
    @Mod.Instance
    public static Wizardry instance;

    public static CreativeTabs tab = new CreativeTabs(MODNAME) {
        @Override
        public String getTabLabel() {
            return MODID;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return ModItems.PHYSICS_BOOK;
        }
    };

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        Config.initConfig();

        ModItems.init();
        ModBlocks.init();
        Fluids.preInit();
        ModRecipes.initCrafting();

        MinecraftForge.EVENT_BUS.register(new EventHandler());

        PacketHandler.INSTANCE.getClass(); // loading the class should be enough to initialize the channel
        proxy.loadModels();

        proxy.preInit(event);
        if (proxy != null) NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        GameRegistry.registerWorldGenerator(new GenHandler(), 0);
        proxy.init(e);

        WizardHandler.INSTANCE.getClass();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

}