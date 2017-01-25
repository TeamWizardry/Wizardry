package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.common.network.PacketBase;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketCapeTick extends PacketBase {

	public PacketCapeTick() {

	}

	@Override
	public void handle(MessageContext messageContext) {
		EntityPlayer player = messageContext.getServerHandler().playerEntity;
		ItemStack cape = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (cape == null) return;
		ItemNBTHelper.setInt(cape, "time", ItemNBTHelper.getInt(cape, "time", 0) + 1);
	}
}
