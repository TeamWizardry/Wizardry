package me.lordsaad.wizardry.event;

import me.lordsaad.wizardry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Saad on 6/20/2016.
 */
public class HudEventHandler extends Gui {

    private ManaBarHandler manaBarHandler;

    public HudEventHandler(ManaBarHandler manaBarHandler) {
        this.manaBarHandler = manaBarHandler;
    }

    @SubscribeEvent
    public void renderHud(RenderGameOverlayEvent.Pre event) {
        ItemStack stack = Minecraft.getMinecraft().thePlayer.getActiveItemStack();
        if (stack == null || stack.getItem() != ModItems.quartzPearl) return;

        manaBarHandler.renderManaBar(event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());
    }
}
