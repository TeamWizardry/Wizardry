package com.teamwizardry.wizardry;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.utilities.UnsafeKt;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.PacketLoggingHandler;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;


/**
 * Created by Saad on 6/9/2016.
 */
@Mod(modid = Wizardry.MODID, version = Wizardry.VERSION, name = Wizardry.MODNAME, useMetadata = true, dependencies = "required-before:librarianlib")
public class Wizardry {

	public static final String MODID = "wizardry";
	public static final String MODNAME = "Wizardry";
	public static final String VERSION = "1.0";
	public static final String CLIENT = "com.teamwizardry.wizardry.proxy.ClientProxy";
	public static final String SERVER = "com.teamwizardry.wizardry.proxy.ServerProxy";
	public static PacketLoggingHandler packetHandler;
	public static Logger logger;
	public static DimensionType underWorld;

	@SidedProxy(clientSide = CLIENT, serverSide = SERVER)
	public static CommonProxy proxy;
	@Mod.Instance
	public static Wizardry instance;

	public static ModCreativeTab tab = new ModCreativeTab(MODNAME) {
		@Override
		@Nonnull
		public ItemStack getIconStack() {
			return new ItemStack(ModItems.BOOK);
		}

		@Nonnull
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ModItems.BOOK);
		}
	};

	static {
		FluidRegistry.enableUniversalBucket();
		UnsafeKt.hookIntoUnsafe();
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
}
