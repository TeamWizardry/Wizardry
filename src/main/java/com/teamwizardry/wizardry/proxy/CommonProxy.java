package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.client.core.CapeHandler;
import com.teamwizardry.wizardry.client.gui.GuiHandler;
import com.teamwizardry.wizardry.common.advancement.AchievementEvents;
import com.teamwizardry.wizardry.common.core.EventHandler;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.module.effects.ModuleEffectLeap;
import com.teamwizardry.wizardry.common.module.effects.ModuleEffectTimeSlow;
import com.teamwizardry.wizardry.common.network.*;
import com.teamwizardry.wizardry.common.world.GenHandler;
import com.teamwizardry.wizardry.common.world.underworld.WorldProviderUnderWorld;
import com.teamwizardry.wizardry.init.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.io.File;

public class CommonProxy {

	public static File directory;

	public void setItemStackHandHandler(EnumHand hand, ItemStack stack) {
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void preInit(FMLPreInitializationEvent event) {
		int tempFix = 42;

		directory = new File(event.getModConfigurationDirectory(), Wizardry.MODID);
		if (!directory.exists()) if (!directory.mkdirs())
			Wizardry.logger.fatal("    > SOMETHING WENT WRONG! Could not create config folder!!");

		new ModTab();
		ModBlocks.init();
		ModItems.init();
		ModSounds.init();
		ModPotions.init();
		ModEntities.init();
		ModCapabilities.preInit();

		WizardryPacketHandler.registerMessages();
		NetworkRegistry.INSTANCE.registerGuiHandler(Wizardry.instance, new GuiHandler());

		Wizardry.underWorld = DimensionType.register("underworld", "_dim", tempFix, WorldProviderUnderWorld.class, false);
		DimensionManager.registerDimension(tempFix, Wizardry.underWorld);

		CapeHandler.INSTANCE.getClass();
		MinecraftForge.EVENT_BUS.register(new WorldProviderUnderWorld());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new AchievementEvents());
		MinecraftForge.EVENT_BUS.register(new ModCapabilities());
		MinecraftForge.EVENT_BUS.register(new ModuleEffectTimeSlow());
		MinecraftForge.EVENT_BUS.register(new ModuleEffectLeap());
		MinecraftForge.EVENT_BUS.register(this);
		SpellTicker.INSTANCE.getClass();

		PacketHandler.register(PacketSendSpellToBook.class, Side.SERVER);
		PacketHandler.register(PacketSyncCape.class, Side.SERVER);

		PacketHandler.register(PacketRenderSpell.class, Side.CLIENT);
		PacketHandler.register(PacketExplode.class, Side.CLIENT);
		PacketHandler.register(PacketSyncModules.class, Side.CLIENT);
		PacketHandler.register(PacketFreezePlayer.class, Side.CLIENT);
		PacketHandler.register(PacketRenderLightningBolt.class, Side.CLIENT);
		PacketHandler.register(PacketSyncCooldown.class, Side.CLIENT);
		PacketHandler.register(PacketVanishPotion.class, Side.CLIENT);
	}

	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new GenHandler(), 0);
	}

	public void postInit(FMLPostInitializationEvent event) {
		ModStructures.INSTANCE.getClass();

		File moduleDirectory = new File(directory, "modules");
		if (!moduleDirectory.exists())
			if (!moduleDirectory.mkdirs()) {
				Wizardry.logger.error("    > SOMETHING WENT WRONG! Could not create directory " + moduleDirectory.getPath());
				return;
			}

		ModuleRegistry.INSTANCE.setDirectory(moduleDirectory);
		ModuleRegistry.INSTANCE.loadUnprocessedModules();
		ModuleRegistry.INSTANCE.copyMissingModulesFromResources(directory);
		ModuleRegistry.INSTANCE.processModules();
	}

	@Nullable
	public ItemStack getCape(EntityPlayer player) {
		for (ItemStack eq : player.getArmorInventoryList()) {
			if ((eq != null) && (eq.getItem() == ModItems.CAPE)) {
				return eq;
			}
		}
		return null;
	}

	@SubscribeEvent
	public void worldJoin(PlayerEvent.PlayerLoggedInEvent event) {
		Wizardry.logger.info("Sending module list to " + event.player.getName());
		//PacketHandler.NETWORK.sendTo(new PacketSyncModules(ModuleRegistry.INSTANCE.modules), (EntityPlayerMP) event.player);
	}
}
