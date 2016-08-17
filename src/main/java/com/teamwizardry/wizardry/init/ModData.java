package com.teamwizardry.wizardry.init;

import com.teamwizardry.wizardry.api.screwcaps.DataFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Saad on 8/17/2016.
 */
public class ModData {

	@SubscribeEvent
	public void onAddCapabilities(AttachCapabilitiesEvent.Entity e) {
		if (e.getEntity() instanceof EntityPlayer) {
			if (!DataFactory.INSTANCE.doesPlayerHaveData((EntityPlayer) e.getEntity()))
				DataFactory.INSTANCE.setDefaultPlayerData((EntityPlayer) e.getEntity());
		}
	}
}
