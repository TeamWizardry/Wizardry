package me.lordsaad.wizardry;

import me.lordsaad.wizardry.particles.SparkleFX;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.PacketLoggingHandler;
import org.apache.logging.log4j.Logger;

/**
 * Created by Saad on 6/9/2016.
 */
@Mod(modid = Wizardry.MODID, version = Wizardry.VERSION, name = Wizardry.MODNAME, useMetadata = true)
public class Wizardry {
    public static final String MODID = "wizardry";
    public static final String MODNAME = "Wizardry";
    public static final String VERSION = "1.0";
    public static PacketLoggingHandler packetHandler;

    @SidedProxy
    public static CommonProxy proxy;

    @Mod.Instance
    public static Wizardry instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
        // ModFluids.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
        // CraftingRecipes.initCrafting();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }


    ///// SERVER  /////
    public static class CommonProxy {
        public void preInit(FMLPreInitializationEvent e) {
            //  PacketHandler.registerMessages();
            //  ModItems.init();
            ModBlocks.init();
        }

        public void init(FMLInitializationEvent e) {
            //  NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        }

        public void postInit(FMLPostInitializationEvent e) {
        }

        public void spawnParticleSparkleLine(World world, double x, double y, double z) {
        }
    }

    ///// CLIENT  /////
    public static class ClientProxy extends CommonProxy {
        @Override
        public void preInit(FMLPreInitializationEvent e) {
            super.preInit(e);
            MinecraftForge.EVENT_BUS.register(this);

            OBJLoader.INSTANCE.addDomain(MODID);
            //  ModItems.initModels();
            ModBlocks.initModels();
        }

        @SubscribeEvent
        public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
            event.getMap().registerSprite(new ResourceLocation(Wizardry.MODID, "entity/sparkle"));
        }

        public void spawnParticleSparkleLine(World world, double x, double y, double z) {
            SparkleFX particle = new SparkleFX(world, x, y, z);
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }

    public static class ServerProxy extends CommonProxy {
    }
}