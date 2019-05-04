package com.teamwizardry.wizardry.common.network.pearlswapping;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlSwappable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketSwapOnRightClick extends PacketBase {

	public PacketSwapOnRightClick() {
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;

		ItemStack swappable = player.getHeldItemMainhand();

		if (swappable.getItem() instanceof IPearlSwappable) {
			((IPearlSwappable) swappable.getItem()).swapOnRightClick(player, swappable);
		}
	}
}
