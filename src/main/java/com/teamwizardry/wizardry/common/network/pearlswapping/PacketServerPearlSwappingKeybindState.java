package com.teamwizardry.wizardry.common.network.pearlswapping;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.init.ModKeybinds;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PacketRegister(Side.SERVER)
public class PacketServerPearlSwappingKeybindState extends PacketBase {

	@Save
	public boolean isKeybindDown;

	public PacketServerPearlSwappingKeybindState() {
	}

	public PacketServerPearlSwappingKeybindState(boolean isKeybindDown) {
		this.isKeybindDown = isKeybindDown;
	}


	@Nullable
	@Override
	public PacketBase reply(@NotNull MessageContext ctx) {
		return new PacketClientPearlSwappingKeybindState(isKeybindDown);
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		ModKeybinds.setPearlSwapping(ctx.getServerHandler().player.getUniqueID(), isKeybindDown);
	}
}
