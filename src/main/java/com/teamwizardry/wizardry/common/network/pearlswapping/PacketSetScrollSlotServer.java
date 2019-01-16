package com.teamwizardry.wizardry.common.network.pearlswapping;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketSetScrollSlotServer extends PacketBase {

	@Save
	public int itemSlot;
	@Save
	public int scrollSlot;

	public PacketSetScrollSlotServer() {
	}

	public PacketSetScrollSlotServer(int itemSlot, int scrollSlot) {

		this.itemSlot = itemSlot;
		this.scrollSlot = scrollSlot;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;

		ItemStack swappable = player.inventory.getStackInSlot(itemSlot);

		ItemNBTHelper.setInt(swappable, "scroll_slot", scrollSlot);

	}
}
