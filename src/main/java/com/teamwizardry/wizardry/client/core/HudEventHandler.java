package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.api.gui.GuiTickHandler;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.client.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.save.IWizardData;
import com.teamwizardry.wizardry.api.save.WizardHandler;
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

    private final Texture HUD_TEXTURE = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/hud.png"));
    private final Sprite emptyManaBar = HUD_TEXTURE.getSprite("mana_empty", 101, 5);
    private final Sprite fullManaBar = HUD_TEXTURE.getSprite("mana_full", 101, 5);
    private final Sprite emptyBurnoutBar = HUD_TEXTURE.getSprite("burnout_empty", 101, 5);
    private final Sprite fullBurnoutBar = HUD_TEXTURE.getSprite("burnout_full", 101, 5);

    @SubscribeEvent
    public void renderHud(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack stack = Minecraft.getMinecraft().thePlayer.getActiveItemStack();

        ScaledResolution resolution = event.getResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {

            int right = (width / 2) - (100 / 2) + 145;
            int top = height - 20;
            HUD_TEXTURE.bind();

            GlStateManager.pushMatrix();
            GlStateManager.color(1F, 1F, 1F);
            emptyManaBar.draw(GuiTickHandler.ticks, right, top);
            emptyBurnoutBar.draw(GuiTickHandler.ticks, right, top + 6);
            GlStateManager.popMatrix();

            IWizardData.BarData data = WizardHandler.getEntityData(player); //// TODO: 7/31/2016

            GlStateManager.pushMatrix();
            GlStateManager.color(1F, 1F, 1F);
            int visualManaLength = 0;
            if (data.manaAmount > 0)
                visualManaLength = (data.manaAmount * 100 / data.manaMax) % 101;
            fullManaBar.drawClipped(GuiTickHandler.ticks, right, top, visualManaLength, 5);

            GlStateManager.color(1F, 1F, 1F);
            int visualBurnoutLength = 0;
            if (data.burnoutAmount > 0)
                visualBurnoutLength = (data.burnoutAmount * 100 / data.burnoutMax) % 101;
            fullBurnoutBar.drawClipped(GuiTickHandler.ticks, right, top + 6, visualBurnoutLength, 5);
            GlStateManager.popMatrix();
        }
    }
}