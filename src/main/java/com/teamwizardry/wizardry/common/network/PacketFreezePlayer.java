package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class PacketFreezePlayer extends PacketBase {

	private int countdown;
	private int interval;

	public PacketFreezePlayer() {

	}

	public PacketFreezePlayer(int countdown, int interval) {
		this.countdown = countdown;
		this.interval = interval;
	}


	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			player.getEntityData().setInteger("strength", countdown);
			player.getEntityData().setInteger("skip_tick", countdown);
			player.getEntityData().setInteger("skip_tick_interval", interval);
			player.getEntityData().setInteger("skip_tick_interval_save", interval);
			player.getEntityData().setDouble("origin_motion_x", player.motionX);
			player.getEntityData().setDouble("origin_motion_y", player.motionY);
			player.getEntityData().setDouble("origin_motion_z", player.motionZ);
		}
	}
}
