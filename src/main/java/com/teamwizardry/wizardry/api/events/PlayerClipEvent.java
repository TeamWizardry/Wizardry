package com.teamwizardry.wizardry.api.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Demoniaque.
 */
public class PlayerClipEvent extends Event {

	public final EntityPlayer player;
	public boolean noClip;

	public PlayerClipEvent(boolean isSpectator, EntityPlayer player) {
		noClip = isSpectator;
		this.player = player;
	}
}
