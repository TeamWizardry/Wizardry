package me.lordsaad.wizardry.book;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import static me.lordsaad.wizardry.book.GuiContentPage.navbarTextures;

/**
 * Created by Saad on 4/19/2016.
 */
class PageBase extends GuiScreen {

    protected static int top;
    protected static int guiWidth = 146, guiHeight = 180;
    protected static int left;
    protected static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/book/book.png");
    protected static boolean hasBookmark = false;
    protected static PageBase bookmarkedPage = null;
    protected static int right;
    protected boolean hasNavBar = false;

    @Override
    public void initGui() {
        super.initGui();
        left = width / 2 - guiWidth / 2;
        top = height / 2 - guiHeight / 2;
        right = (width / 2 + guiWidth / 2) - 6;
        Button bookmark = new Button(3, 0, 0, 5, 5);
        buttonList.add(bookmark);
    }

    protected void enableNavBar(boolean enable) {
        buttonList.clear();
        if (enable) {
            hasNavBar = true;
            Button BACK, NEXT, TOINDEX;
            buttonList.add(BACK = new Button(0, 0, 0, 7, 12));
            buttonList.add(NEXT = new Button(1, 0, 0, 7, 12));
            buttonList.add(TOINDEX = new Button(2, 0, 0, 13, 19));

            ResourceLocation back = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/left_arrow.png");
            ResourceLocation next = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/right_arrow.png");
            ResourceLocation toIndex = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/to_index.png");
            navbarTextures.put(TOINDEX, toIndex);
            navbarTextures.put(BACK, back);
            navbarTextures.put(NEXT, next);
        } else hasNavBar = false;
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
        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.setBidiFlag(true);
        if (hasNavBar) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".");
            GlStateManager.color(1F, 1F, 1F, 1F);
            int y = top + 190;
            mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
            drawTexturedModalRect((width / 2) - 66, y - 2, 19, 182, 133, 14);
            for (GuiButton button : buttonList) {
                if (navbarTextures.containsKey(button)) {

                    mc.renderEngine.bindTexture(navbarTextures.get(button));
                    boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + button.width && mouseY >= button.yPosition && mouseY < button.yPosition + button.height;
                    if (inside) GlStateManager.color(20F, 100F, 135F, 1F);
                    else GlStateManager.color(0F, 170F, 255F, 1F);

                    switch (button.id) {
                        case 0:
                            button.xPosition = left + 12;
                            button.yPosition = y;
                            button.drawButton(mc, left + 12, y);
                            drawScaledCustomSizeModalRect(left + 12, y, 0, 0, 6, 10, 6, 10, 6, 10);
                            break;
                        case 1:
                            button.xPosition = right - 12;
                            button.yPosition = y;
                            button.drawButton(mc, right - 12, y);
                            drawScaledCustomSizeModalRect(right - 12, y, 0, 0, 6, 10, 6, 10, 6, 10);
                            break;
                        case 2:
                            button.xPosition = width / 2 - 6;
                            button.yPosition = y;
                            button.drawButton(mc, width / 2 - 6, y);
                            drawScaledCustomSizeModalRect(width / 2 - 6, y, 0, 0, 12, 11, 12, 11, 12, 11);
                            break;
                    }
                }
            }
        }
        fontRendererObj.setUnicodeFlag(false);
        fontRendererObj.setBidiFlag(false);
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
