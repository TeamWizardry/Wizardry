package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.DefaultWizardryCapability;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Saad on 8/16/2016.
 */
public class ModCapabilities {

	public static void preInit(){
		CapabilityManager.INSTANCE.register(IWizardryCapability.class, new WizardryCapabilityStorage(), DefaultWizardryCapability.class);
	}

	@SubscribeEvent
	public void onAddCapabilities(AttachCapabilitiesEvent.Entity e){
		if (e.getEntity() instanceof EntityPlayer){
			if(!e.getEntity().hasCapability(WizardryCapabilityProvider.wizardryCapability, null)){
				e.addCapability(new ResourceLocation(Wizardry.MODID, "capabilities"), new WizardryCapabilityProvider(new DefaultWizardryCapability()));
			}
		}
	}
}
