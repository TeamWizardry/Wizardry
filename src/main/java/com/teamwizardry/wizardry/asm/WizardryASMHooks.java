package com.teamwizardry.wizardry.asm;

import com.teamwizardry.wizardry.api.events.PlayerClipEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by LordSaad.
 */
public class WizardryASMHooks {

	public static boolean playerClipEventHook(boolean isSpectator, EntityPlayer player) {
		PlayerClipEvent event = new PlayerClipEvent(player);
		MinecraftForge.EVENT_BUS.post(event);
		return event.noClip;
	}
}
