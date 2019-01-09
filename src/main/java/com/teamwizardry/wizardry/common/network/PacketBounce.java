package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketBounce extends PacketBase {

	public PacketBounce() {
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		ctx.getServerHandler().player.fallDistance = 0;
	}
}
