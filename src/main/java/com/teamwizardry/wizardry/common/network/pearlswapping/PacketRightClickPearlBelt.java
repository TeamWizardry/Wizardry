package com.teamwizardry.wizardry.common.network.pearlswapping;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketRightClickPearlBelt extends PacketBase {

	@Save
	public boolean isKeybindDown;

	public PacketRightClickPearlBelt() {
	}

	public PacketRightClickPearlBelt(boolean isKeybindDown) {
		this.isKeybindDown = isKeybindDown;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;

		ItemStack swappable = player.getHeldItemMainhand();

		if (swappable.getItem() instanceof IPearlBelt) {
			((IPearlBelt) swappable.getItem()).onRightClick(player.world, player, player.getActiveHand(), isKeybindDown);
		}
	}
}
