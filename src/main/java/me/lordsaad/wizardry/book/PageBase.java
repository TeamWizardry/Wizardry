package me.lordsaad.wizardry.book;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import static me.lordsaad.wizardry.book.GuiContentPage.hoverTextures;
import static me.lordsaad.wizardry.book.GuiContentPage.regularTextures;

/**
 * Created by Saad on 4/19/2016.
 */
public class PageBase extends GuiScreen {

    protected static int top;
    static int guiWidth = 146, guiHeight = 180;
    static int left;
    static int right;
    static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/book/book.png");
    static boolean hasBookmark = false;
    static PageBase bookmarkedPage = null;
    private boolean hasNavBar = false;

    @Override
    public void initGui() {
        super.initGui();
        left = width / 2 - guiWidth / 2;
        top = height / 2 - guiHeight / 2;
        right = (width / 2 + guiWidth / 2) - 6;
    }

    public void enableNavBar(boolean enable) {
        buttonList.clear();
        if (enable) {
            hasNavBar = true;
            Button BACK, NEXT, TOINDEX;
            buttonList.add(BACK = new Button(0, 0, 0, 7, 12));
            buttonList.add(NEXT = new Button(1, 0, 0, 7, 12));
            buttonList.add(TOINDEX = new Button(2, 0, 0, 13, 19));

            ResourceLocation back = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/left_arrow.png");
            ResourceLocation back_hover = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/hover_left_arrow.png");
            ResourceLocation next = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/right_arrow.png");
            ResourceLocation next_hover = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/hover_right_arrow.png");
            ResourceLocation toIndex = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/to_index.png");
            ResourceLocation toIndex_hover = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/hover_to_index.png");
            regularTextures.put(TOINDEX, toIndex);
            regularTextures.put(BACK, back);
            regularTextures.put(NEXT, next);
            hoverTextures.put(TOINDEX, toIndex_hover);
            hoverTextures.put(BACK, back_hover);
            hoverTextures.put(NEXT, next_hover);
        } else hasNavBar = false;
    }

    public void renderBookmark(int y, boolean withStripe) {
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
            GlStateManager.color(1F, 1F, 1F, 1F);
            int y = (int) (top * 3.2);
            mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
            drawTexturedModalRect((width / 2) - 66, y - 2, 19, 182, 133, 14);
            for (GuiButton button : buttonList) {
                if (hoverTextures.containsKey(button) && regularTextures.containsKey(button)) {

                    boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + button.width && mouseY >= button.yPosition && mouseY < button.yPosition + button.height;
                    if (inside) mc.renderEngine.bindTexture(hoverTextures.get(button));
                    else mc.renderEngine.bindTexture(regularTextures.get(button));

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
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    @Override
    public void onGuiClosed() {
        Tippable.ID.clear();

    }
}
