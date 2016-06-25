package com.teamwizardry.wizardry.event;

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

    private final ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "textures/gui/hud.png");

    @SubscribeEvent
    public void renderHud(RenderGameOverlayEvent.Post event) {
        ItemStack stack = Minecraft.getMinecraft().thePlayer.getActiveItemStack();
        //       if (stack == null || stack.getItem() != ModItems.quartzPearl) return;

        ScaledResolution resolution = event.getResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {

            int left = width / 2 - 40;
            int top = height - 52;

            GlStateManager.pushMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 0, 0, 100, 5);
            GlStateManager.popMatrix();
        }
    }
}
