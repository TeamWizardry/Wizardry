package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Saad on 8/18/2016.
 */
public class ModCapabilities {

	public static void preInit() {
		CapabilityManager.INSTANCE.register(IWizardryCapability.class, new WizardryCapabilityStorage(), DefaultWizardryCapability.class);
	}

	@SubscribeEvent
	public void onAddCapabilities(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			WizardryCapabilityProvider cap = new WizardryCapabilityProvider(new DefaultWizardryCapability());
			e.addCapability(new ResourceLocation(Wizardry.MODID, "capabilities"), cap);
		}
	}

	@SubscribeEvent
	public void worldJoin(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {

			Thread thread = new Thread(() -> {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				new CapManager(event.getEntity()).sync();
			});
			thread.start();
		}
	}

	@SubscribeEvent
	public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
		new CapManager(event.getTarget()).sync();
	}

	@SubscribeEvent
	public void onPlayerLogin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
		new CapManager(event.player).sync();
	}

	@SubscribeEvent
	public void onPlayerChangeDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
		new CapManager(event.player).sync();
	}

	@SubscribeEvent
	public void onPlayerSpawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
		new CapManager(event.player).sync();
	}
}
