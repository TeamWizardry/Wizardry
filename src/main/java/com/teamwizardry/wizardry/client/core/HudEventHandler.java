package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.client.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.gui.IWizardData;
import com.teamwizardry.wizardry.api.gui.WizardHandler;
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

    private final Texture HUD_TEXTURE = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/hud.png"), 256, 256);

    @SubscribeEvent
    public void renderHud(RenderGameOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack stack = Minecraft.getMinecraft().thePlayer.getActiveItemStack();

        ScaledResolution resolution = event.getResolution();
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {

            int left = (width / 2) - (100 / 2) - 150;
            int right = (width / 2) - (100 / 2) + 150;
            int top = height - 29;
            Sprite emptyManaBar = new Sprite(HUD_TEXTURE, 0, 0, 101, 5);
            Sprite fullManaBar = new Sprite(HUD_TEXTURE, 0, 5, 101, 10);
            Sprite emptyBurnoutBar = new Sprite(HUD_TEXTURE, 0, 10, 101, 15);
            Sprite fullBurnoutBar = new Sprite(HUD_TEXTURE, 0, 15, 101, 20);
            HUD_TEXTURE.bind();

            GlStateManager.pushMatrix();
            GlStateManager.color(1F, 1F, 1F);
            emptyManaBar.draw(right, top);
            emptyBurnoutBar.draw(left, top);
            GlStateManager.popMatrix();

            IWizardData.BarData data = WizardHandler.getEntityData(player);

            // MANA
            GlStateManager.pushMatrix();
            GlStateManager.color(1F, 1F, 1F);
            int visualLength = 0;
            if (data.manaAmount > 0)
                visualLength = 101 - (data.manaMax / data.manaAmount);
            fullManaBar.drawClipped(right, top, visualLength, 5);
            GlStateManager.popMatrix();
        }
    }
}
