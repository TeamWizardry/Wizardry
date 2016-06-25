package com.teamwizardry.wizardry;

import com.teamwizardry.wizardry.bars.WizardHandler;
import com.teamwizardry.wizardry.event.EventHandler;
import com.teamwizardry.wizardry.fluid.Fluids;
import com.teamwizardry.wizardry.gui.GuiHandler;
import com.teamwizardry.wizardry.network.PacketHandler;
import com.teamwizardry.wizardry.world.GenHandler;
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
    public static final String CLIENT = "ClientProxy";
    public static final String SERVER = "CommonProxy";
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
            return ModItems.physicsBook;
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
        CraftingRecipes.initCrafting();

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