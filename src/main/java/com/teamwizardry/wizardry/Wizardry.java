package com.teamwizardry.wizardry;

import com.teamwizardry.librarianlib.api.LibrarianLog;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.book.Book;
import com.teamwizardry.wizardry.api.Config;
import com.teamwizardry.wizardry.api.capability.WizardHandler;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.SpellHandler;
import com.teamwizardry.wizardry.api.trackerobject.SpellTracker;
import com.teamwizardry.wizardry.client.gui.GuiHandler;
import com.teamwizardry.wizardry.common.achievement.AchievementEvents;
import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.core.EventHandler;
import com.teamwizardry.wizardry.common.fluid.Fluids;
import com.teamwizardry.wizardry.common.proxy.CommonProxy;
import com.teamwizardry.wizardry.common.world.GenHandler;
import com.teamwizardry.wizardry.init.*;
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
@Mod(modid = Wizardry.MODID, version = Wizardry.VERSION, name = Wizardry.MODNAME, useMetadata = true, dependencies = "required-after:librarianlib")
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
    
    public static Book guide;
    
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
        LibrarianLog.I.info("o͡͡͡╮༼ ಠДಠ ༽╭o͡͡͡━☆ﾟ.*･｡ﾟ IT'S LEVI-OH-SA, NOT LEVIOSAA");

        logger = event.getModLog();
        guide = new Book(MODID);
        guide.setColor(Color.rgb(0x1AFF00));
        Config.initConfig();

        ModSounds.init();
        ModItems.init();
        ModBlocks.init();
        Fluids.preInit();
        ModRecipes.initCrafting();
        Achievements.init();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new AchievementEvents());

        proxy.loadModels();

        proxy.preInit(event);
        if (proxy != null) NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        GameRegistry.registerWorldGenerator(new GenHandler(), 0);
        proxy.init(e);

        WizardHandler.INSTANCE.getClass();
        ModuleRegistry.getInstance().getClass();
        SpellHandler.INSTANCE.getClass();
        ModModules.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
        
        SpellTracker.init();
    }

}