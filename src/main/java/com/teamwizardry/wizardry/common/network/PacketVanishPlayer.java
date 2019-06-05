package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.common.module.effects.vanish.VanishTracker;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@PacketRegister(Side.CLIENT)
public class PacketVanishPlayer extends PacketBase {

	@Save
	public int entityId;
	@Save
	public int time;

	public PacketVanishPlayer() {
	}

	public PacketVanishPlayer(int entityId, int time) {
		this.entityId = entityId;
		this.time = time;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handle(@Nonnull MessageContext ctx) {
		VanishTracker.addVanishObject(entityId, time);
	}
}
