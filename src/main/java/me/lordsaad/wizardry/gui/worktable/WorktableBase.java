package me.lordsaad.wizardry.gui.worktable;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableBase extends GuiScreen {

    public static int left, top, right;
    public static int backgroundWidth = 136, backgroundHeight = 119; // SIZE OF PAPER
    public static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sample-page-background.png");

    @Override
    public void initGui() {
        super.initGui();
        left = width / 2 - backgroundWidth / 2;
        top = height / 2 - backgroundHeight / 2;
        right = (width / 2 + backgroundWidth / 2) - 6;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // RENDER BACKGROUND //
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(left, top, 0, 0, backgroundWidth, backgroundHeight);
        // RENDER BACKGROUND
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

}
