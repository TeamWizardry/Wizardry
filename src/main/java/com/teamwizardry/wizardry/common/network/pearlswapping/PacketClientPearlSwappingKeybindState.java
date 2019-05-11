package com.teamwizardry.wizardry.common.network.pearlswapping;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.init.ModKeybinds;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketClientPearlSwappingKeybindState extends PacketBase {

	@Save
	public boolean isKeybindDown;

	public PacketClientPearlSwappingKeybindState() {
	}

	public PacketClientPearlSwappingKeybindState(boolean isKeybindDown) {
		this.isKeybindDown = isKeybindDown;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		ModKeybinds.setPearlSwapping(Minecraft.getMinecraft().player.getUniqueID(), isKeybindDown);
	}
}
