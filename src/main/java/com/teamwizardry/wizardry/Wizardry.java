package com.teamwizardry.wizardry;

import com.teamwizardry.librarianlib.features.utilities.UnsafeKt;
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
import org.apache.logging.log4j.Logger;


/**
 * Created by Demoniaque on 6/9/2016.
 */
@Mod(modid = Wizardry.MODID, version = Wizardry.VERSION, name = Wizardry.MODNAME, dependencies = Wizardry.DEPENDENCIES)
public class Wizardry {

	public static final String MODID = "wizardry";
	public static final String MODNAME = "Wizardry";
	public static final String VERSION = "GRADLE:VERSION";
	public static final String CLIENT = "com.teamwizardry.wizardry.proxy.ClientProxy";
	public static final String SERVER = "com.teamwizardry.wizardry.proxy.ServerProxy";
	public static final String DEPENDENCIES = "required-before:librarianlib";
	public static Logger logger;
	public static DimensionType underWorld;

	@SidedProxy(clientSide = CLIENT, serverSide = SERVER)
	public static CommonProxy proxy;
	@Mod.Instance
	public static Wizardry instance;

	static {
		UnsafeKt.hookIntoUnsafe();
		FluidRegistry.enableUniversalBucket();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		Wizardry.logger.info("o͡͡͡╮༼ ಠДಠ ༽╭o͡͡͡━☆ﾟ.*･｡ﾟ IT'S LEVI-OH-SA, NOT LEVIOSAA");

		logger = event.getModLog();

		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandWizardry());
	}
}
