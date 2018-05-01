package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.DefaultWizardryCapability;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Demoniaque on 8/18/2016.
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
	
	@SubscribeEvent
	public void onAddCapabilitiesOnVanilla(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack itemStack = event.getObject();
		if( itemStack.getItem() == Items.BOOK || itemStack.getItem() == Items.ENCHANTED_BOOK ) {
			if( itemStack.hasCapability(WizardryCapabilityProvider.wizardryCapability, null) )
				return;
			event.addCapability(
					new ResourceLocation(Wizardry.MODID, "wizcaps"),
					new WizardryCapabilityProvider(
							new CustomWizardryCapability(300, 300, 0, 0)));
		}
	}
}
