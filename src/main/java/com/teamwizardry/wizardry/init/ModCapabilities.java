package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
}
