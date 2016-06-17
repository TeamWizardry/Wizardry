package me.lordsaad.wizardry;

import org.apache.logging.log4j.Logger;

import me.lordsaad.wizardry.event.EventHandler;
import me.lordsaad.wizardry.gui.GuiHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.PacketLoggingHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/9/2016.
 */
@Mod(modid = Wizardry.MODID, version = Wizardry.VERSION, name = Wizardry.MODNAME, useMetadata = true)
public class Wizardry {
    public static final String MODID = "wizardry";
    public static final String MODNAME = "Wizardry";
    public static final String VERSION = "1.0";
    public static final String CLIENT = "me.lordsaad.wizardry.client.ClientProxy";
    public static final String SERVER = "me.lordsaad.wizardry.CommonProxy";
    public static PacketLoggingHandler packetHandler;

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

    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    public static CommonProxy proxy;

    @Mod.Instance
    public static Wizardry instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        
        ModItems.init();
        ModBlocks.init();
        
        CraftingRecipes.initCrafting();
        
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        
        proxy.loadModels();
        
        proxy.preInit(event);
        if (proxy != null)
		{
        	NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		}
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) 
    {
    	proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

}