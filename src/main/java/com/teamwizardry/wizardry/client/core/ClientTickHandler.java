package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.common.network.PacketCapeOwnerTransfer;
import com.teamwizardry.wizardry.common.network.PacketCapeTick;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by LordSaad.
 */
public class ClientTickHandler {

	public static final ClientTickHandler INSTANCE = new ClientTickHandler();
	public static int tick;

	private ClientTickHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		ItemStack cape = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (cape == null) return;
		if (cape.getItem() != ModItems.CAPE) return;

		if (tick < 1200) tick++;
		else {
			tick = 0;

			Minecraft.getMinecraft().player.sendChatMessage(ItemNBTHelper.getInt(cape, "time", 0) + "");
			PacketHandler.NETWORK.sendToServer(new PacketCapeTick());

			if (ItemNBTHelper.getInt(cape, "owner", -1) != player.getEntityId() && ItemNBTHelper.getInt(cape, "time", 0) >= 60)
				PacketHandler.NETWORK.sendToServer(new PacketCapeOwnerTransfer());
		}
	}
}
