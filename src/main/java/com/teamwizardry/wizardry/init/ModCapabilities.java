package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.DefaultWizardryCapability;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Saad on 8/18/2016.
 */
public class ModCapabilities {

	public static void preInit() {
		CapabilityManager.INSTANCE.register(IWizardryCapability.class, new WizardryCapabilityStorage(), DefaultWizardryCapability.class);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onAddCapabilities(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			WizardryCapabilityProvider cap = new WizardryCapabilityProvider(new DefaultWizardryCapability());
			e.addCapability(new ResourceLocation(Wizardry.MODID, "capabilities"), cap);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
		//if (!event.getTarget().world.isRemote) {
		//	new CapManager(event.getTarget()).sync(true);
		//}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerLogin(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
		//if (!event.player.world.isRemote) {
		//	new CapManager(event.player).sync(true);
		//}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerChangeDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
		//if (!event.player.world.isRemote)
		//	new CapManager(event.player).sync(true);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerSpawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
		//if (!event.player.world.isRemote)
		//	new CapManager(event.player).sync(true);
	}
}
