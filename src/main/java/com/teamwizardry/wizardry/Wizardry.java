package com.teamwizardry.wizardry;

import com.teamwizardry.wizardry.common.command.CommandWizardry;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Created by Demoniaque on 6/9/2016.
 */
@Mod(modid = Wizardry.MODID, version = Wizardry.VERSION, name = Wizardry.MODNAME, dependencies = Wizardry.DEPENDENCIES)
public class Wizardry {
    public static final String MODID = "wizardry";
    public static final String MODNAME = "Wizardry";
    public static final String VERSION = "@VERSION@";
    public static final String CLIENT = "com.teamwizardry.wizardry.proxy.ClientProxy";
    public static final String SERVER = "com.teamwizardry.wizardry.proxy.ServerProxy";
    public static final String DEPENDENCIES = "required-after:librarianlib";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static DimensionType underWorld;
    public static DimensionType torikki;
    @SidedProxy(clientSide = CLIENT, serverSide = SERVER)
    public static CommonProxy PROXY;
    @Mod.Instance
    public static Wizardry instance;

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Wizardry.LOGGER.info("IT'S LEVI-OH-SA, NOT LEVIOSAA");

	    PROXY.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
	    PROXY.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
	    PROXY.postInit(e);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandWizardry());
    }
}
