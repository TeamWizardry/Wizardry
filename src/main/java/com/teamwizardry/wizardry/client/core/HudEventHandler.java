package com.teamwizardry.wizardry.client.core;

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
public class HudEventHandler extends Gui {

    private final ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "textures/gui/book/sliders.png");

    @SubscribeEvent
    public void renderHud(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack stack = Minecraft.getMinecraft().thePlayer.getActiveItemStack();

        ScaledResolution resolution = event.getResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {

            int left = width / 2 - 40;
            int top = height - 52;

            GlStateManager.pushMatrix();
            GlStateManager.color(1F, 1F, 1F);
            mc.renderEngine.bindTexture(texture);
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow("LOREM IPSUM DOLOR SIT AMET", left, top, 0);
            //Gui.drawRect(left, top, 50, 50, Color.BLACK.getRGB());
            drawTexturedModalRect(left, top, 0, 0, 100, 50);
            GlStateManager.popMatrix();
        }
    }
}
