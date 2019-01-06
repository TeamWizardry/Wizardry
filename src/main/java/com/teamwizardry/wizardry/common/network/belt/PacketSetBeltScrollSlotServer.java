package com.teamwizardry.wizardry.common.network.belt;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.SERVER)
public class PacketSetBeltScrollSlotServer extends PacketBase {

	@Save
	public int itemSlot;
	@Save
	public int scrollSlot;

	public PacketSetBeltScrollSlotServer() {
	}

	public PacketSetBeltScrollSlotServer(int itemSlot, int scrollSlot) {

		this.itemSlot = itemSlot;
		this.scrollSlot = scrollSlot;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;

		ItemStack belt = player.inventory.getStackInSlot(itemSlot);
		if (belt.getItem() != ModItems.PEARL_BELT) return;

		ItemNBTHelper.setInt(belt, "scroll_slot", scrollSlot);

	}
}
