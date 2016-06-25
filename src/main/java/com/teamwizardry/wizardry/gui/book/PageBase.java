package com.teamwizardry.wizardry.gui.book;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.gui.book.util.Color;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;


/**
 * Created by Saad on 4/19/2016.
 */
class PageBase extends GuiScreen {

    public static int bookBackgroundWidth = 146, bookBackgroundHeight = 180;
    public static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/book/book.png");
    protected static int top, left, right;
    static boolean hasBookmark = false;
    static PageBase bookmarkedPage = null;
    protected String title = I18n.format("gui.physicsbook.defaulttitle");
    private boolean hasNavPrev = false, hasNavNext = false, hasNavReturn = false;
    private GuiButton backButton, nextButton, returnButton;

    @Override
    public void initGui() {
        super.initGui();
        left = width / 2 - bookBackgroundWidth / 2;
        top = height / 2 - bookBackgroundHeight / 2;
        right = (width / 2 + bookBackgroundWidth / 2) - 6;
        buttonList.add(new Button(Constants.GuiButtons.BOOKMARK, 0, 0, 5, 5)); // bookmark button

        backButton = new Button(Constants.GuiButtons.NAV_BAR_BACK, 0, 0, 18, 10); // nav bar back button
        buttonList.add(backButton);
        nextButton = new Button(Constants.GuiButtons.NAV_BAR_NEXT, 0, 0, 18, 10); // nav bar next button
        buttonList.add(nextButton);
        returnButton = new Button(Constants.GuiButtons.NAV_BAR_INDEX, 0, 0, 17, 10); // nav bar index button
        buttonList.add(returnButton);
    }

    protected boolean isNavBarEnabled() {
        return hasNavPrev || hasNavNext || hasNavReturn;
    }

    public boolean hasNavPrev() {
        return hasNavPrev;
    }

    public boolean hasNavNext() {
        return hasNavNext;
    }

    public boolean hasNavReturn() {
        return hasNavReturn;
    }

    public void setHasNavPrev(boolean hasNavPrev) {
        this.hasNavPrev = hasNavPrev;
    }

    public void setHasNavNext(boolean hasNavNext) {
        this.hasNavNext = hasNavNext;
    }

    public void setHasNavReturn(boolean hasNavReturn) {
        this.hasNavReturn = hasNavReturn;
    }

    protected void renderBookmark(int y, boolean withStripe) {
        buttonList.stream().filter(button -> button.id == 3).forEach(button -> {
            mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
            GlStateManager.color(1F, 1F, 1F, 1F);
            button.xPosition = left + 120;
            button.yPosition = top + 8;
            button.drawButton(mc, left + 120, top + 8);
        });
        GlStateManager.color(1F, 1F, 1F, 1F);
        drawTexturedModalRect(left + 120, top + 8, 0, 180, 7, 7);
        if (hasBookmark) {
            GlStateManager.color(1F, 1F, 1F, 1F);
            drawTexturedModalRect(left + 14, y, 152, 0, 16, 195);
            if (withStripe) drawTexturedModalRect(left + 117, top + 6, 0, 187, 14, 13);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        backButton.enabled = hasNavPrev;
        nextButton.enabled = hasNavNext;
        returnButton.enabled = hasNavReturn;

        // RENDER HEADER //
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);

        drawTexturedModalRect((width / 2) - (133 / 2), (float) (top - 20), 19, 182, 133, 14);
        fontRendererObj.setUnicodeFlag(false);
        fontRendererObj.setBidiFlag(false);
        int titleWidth = fontRendererObj.getStringWidth(title);
        fontRendererObj.drawString(title, (width / 2) - titleWidth / 2, (float) (top - 20) + 4, 0, false);
        // RENDER HEADER //

        // RENDER BOOK BACKGROUND //
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(left, top, 0, 0, bookBackgroundWidth, bookBackgroundHeight);
        // RENDER BOOK BACKGROUND

        // RENDER NAV BAR //
        if (isNavBarEnabled()) {
            int y = top + 190;

            // render the navbar bar
            drawTexturedModalRect((width / 2) - (133 / 2), y - 2, 19, 182, 133, 14);

            Color
                    disabledColor = Color.rgb(0xB0B0B0),
                    hoverColor = Color.rgb(0x0DDED3),
                    normalColor = Color.rgb(0x0DBFA2);

            // render navbar buttons
            for (GuiButton button : buttonList) {
                switch (button.id) {
                    case Constants.GuiButtons.NAV_BAR_BACK: {
                        button.xPosition = left + 15;
                        button.yPosition = y;
                        button.drawButton(mc, button.xPosition, button.yPosition);

                        boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + 18 && mouseY >= button.yPosition && mouseY < button.yPosition + 10;
                        if (!button.enabled) disabledColor.glColor();
                        else if (inside) hoverColor.glColor();
                        else normalColor.glColor();

                        drawTexturedModalRect(button.xPosition, button.yPosition, 0, 209, 18, 10);
                        break;
                    }
                    case Constants.GuiButtons.NAV_BAR_NEXT: {
                        button.xPosition = right - 26;
                        button.yPosition = y;
                        button.drawButton(mc, button.xPosition, button.yPosition);

                        boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + 18 && mouseY >= button.yPosition && mouseY < button.yPosition + 10;
                        if (!button.enabled) disabledColor.glColor();
                        else if (inside) hoverColor.glColor();
                        else normalColor.glColor();

                        drawTexturedModalRect(button.xPosition, button.yPosition, 0, 199, 18, 10);
                        break;
                    }
                    case Constants.GuiButtons.NAV_BAR_INDEX: {
                        button.xPosition = (width / 2) - (17 / 2);
                        button.yPosition = y + 1;
                        button.drawButton(mc, button.xPosition, button.yPosition);

                        boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + 18 && mouseY >= button.yPosition && mouseY < button.yPosition + 10;
                        if (!button.enabled) disabledColor.glColor();
                        else if (inside) hoverColor.glColor();
                        else normalColor.glColor();

                        drawTexturedModalRect(button.xPosition, button.yPosition, 0, 219, 17, 10);
                        break;
                    }
                }
            }
        }
        // RENDER NAV BAR //

    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void onGuiClosed() {
        Tippable.clearTips();
    }
}
