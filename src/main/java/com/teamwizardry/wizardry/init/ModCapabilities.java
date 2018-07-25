package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.mana.DefaultWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Created by Demoniaque on 8/18/2016.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class ModCapabilities {

	public static void preInit() {
		CapabilityManager.INSTANCE.register(IWizardryCapability.class, new WizardryCapabilityStorage(), DefaultWizardryCapability.class);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void attachEntity(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			WizardryCapabilityProvider cap = new WizardryCapabilityProvider(new DefaultWizardryCapability());
			e.addCapability(new ResourceLocation(Wizardry.MODID, "capabilities"), cap);
		}
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player.world.isRemote) return;

		IWizardryCapability cap = WizardryCapabilityProvider.getCap(event.player);
		if (cap == null) return;
		cap.dataChanged(event.player);
	}

	@SubscribeEvent
	public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.player.world.isRemote) return;

		IWizardryCapability cap = WizardryCapabilityProvider.getCap(event.player);
		if (cap == null) return;
		cap.dataChanged(event.player);
	}

	@SubscribeEvent
	public static void onDimChange(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.player.world.isRemote) return;

		IWizardryCapability cap = WizardryCapabilityProvider.getCap(event.player);
		if (cap == null) return;
		cap.dataChanged(event.player);
	}
}
