package com.teamwizardry.wizardry.api.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LordSaad.
 */
public class PlayerClipEvent extends Event {

	public EntityPlayer player;
	public boolean noClip = false;

	public PlayerClipEvent(EntityPlayer player) {
		this.player = player;
	}
}
