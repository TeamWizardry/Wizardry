package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.init.ModItems;
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

            ItemNBTHelper.setInt(cape, "time", ItemNBTHelper.getInt(cape, "time", 0) + 1);
        }
    }
}
