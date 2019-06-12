package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.player.mana.DefaultManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.IManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaCapabilityProvider;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaCapabilityStorage;
import com.teamwizardry.wizardry.api.capability.player.miscdata.DefaultMiscCapability;
import com.teamwizardry.wizardry.api.capability.player.miscdata.IMiscCapability;
import com.teamwizardry.wizardry.api.capability.player.miscdata.MiscCapabilityProvider;
import com.teamwizardry.wizardry.api.capability.player.miscdata.MiscCapabilityStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Created by Demoniaque on 8/18/2016.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class ModCapabilities {

	public static void preInit() {
		CapabilityManager.INSTANCE.register(IManaCapability.class, new ManaCapabilityStorage(), DefaultManaCapability::new);
		CapabilityManager.INSTANCE.register(IMiscCapability.class, new MiscCapabilityStorage(), DefaultMiscCapability::new);
	}

	@SubscribeEvent
	public static void attachEntity(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			ManaCapabilityProvider manaCap = new ManaCapabilityProvider(new DefaultManaCapability());
			e.addCapability(new ResourceLocation(Wizardry.MODID, "capability_mana"), manaCap);

			MiscCapabilityProvider miscCap = new MiscCapabilityProvider(new DefaultMiscCapability());
			e.addCapability(new ResourceLocation(Wizardry.MODID, "capability_misc"), miscCap);
		}
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.player.world.isRemote) return;

		IManaCapability manaCap = ManaCapabilityProvider.getCap(event.player);
		if (manaCap != null)
			manaCap.dataChanged(event.player);

		IMiscCapability miscCap = MiscCapabilityProvider.getCap(event.player);
		if (miscCap != null)
			miscCap.dataChanged(event.player);
	}

	@SubscribeEvent
	public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.player.world.isRemote) return;

		IManaCapability manaCap = ManaCapabilityProvider.getCap(event.player);
		if (manaCap != null)
			manaCap.dataChanged(event.player);

		IMiscCapability miscCap = MiscCapabilityProvider.getCap(event.player);
		if (miscCap != null)
			miscCap.dataChanged(event.player);
	}

	@SubscribeEvent
	public static void onDimChange(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.player.world.isRemote) return;

		IManaCapability manaCap = ManaCapabilityProvider.getCap(event.player);
		if (manaCap != null)
			manaCap.dataChanged(event.player);

		IMiscCapability miscCap = MiscCapabilityProvider.getCap(event.player);
		if (miscCap != null)
			miscCap.dataChanged(event.player);
	}
}
