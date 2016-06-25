package com.teamwizardry.wizardry.event;

import com.teamwizardry.wizardry.ModItems;
import com.teamwizardry.wizardry.Utils;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Saad on 6/20/2016.
 */
public class HudEventHandler {

    private final ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "textures/gui/hud/bar_hud_outline.png");

    @SubscribeEvent
    public void renderHud(RenderGameOverlayEvent.Post event) {
        ItemStack stack = Minecraft.getMinecraft().thePlayer.getActiveItemStack();
        if (stack == null || stack.getItem() != ModItems.quartzPearl) return;

        ScaledResolution resolution = event.getResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        Minecraft.getMinecraft().thePlayer.sendChatMessage(":)");
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

            int left = width / 2 - 8;
            int top = height - 52;

            GlStateManager.pushMatrix();
            Gui.drawModalRectWithCustomSizedTexture(left, top, 0, 0, 200, 50, 200, 50);
            Utils.drawTexturedModalRect(left, top, 0, 0, 100, 51);
            GlStateManager.popMatrix();
        }
    }
}
