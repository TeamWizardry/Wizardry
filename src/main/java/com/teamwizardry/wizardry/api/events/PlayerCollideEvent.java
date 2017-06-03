package com.teamwizardry.wizardry.api.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by LordSaad.
 */
public class PlayerCollideEvent extends Event {

	private EntityPlayer player;

	public PlayerCollideEvent(EntityPlayer player) {
		this.player = player;
	}
}
