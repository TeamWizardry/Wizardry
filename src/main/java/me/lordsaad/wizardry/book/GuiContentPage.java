package me.lordsaad.wizardry.book;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saad on 5/1/2016.
 */
public class GuiContentPage extends Tippable {

    protected static HashMap<Integer, ArrayList<String>> pages;
    protected static int currentPage = 0;
    protected static HashMap<GuiButton, ResourceLocation> navbarTextures;
    protected boolean hasBookmark = PageBase.hasBookmark;
    protected int pageID;

    @Override
    public void initGui() {
        super.initGui();
        pages = new HashMap<>();
        navbarTextures = new HashMap<>();
        clearTips();
        enableNavBar(true);
        pageID = 0;
        buttonList.add(new Button(3, 0, 0, 8, 8));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                if (currentPage > 0) {
                    currentPage--;
                    mc.thePlayer.openGui(Wizardry.instance, pageID, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                } else {
                    mc.thePlayer.openGui(Wizardry.instance, GuiHandler.INDEX, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                    currentPage = 0;
                }
                break;
            }
            case 1: {
                if (pages.size() >= currentPage) {
                    currentPage++;
                    mc.thePlayer.openGui(Wizardry.instance, pageID, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                }
                break;
            }
            case 2: {
                mc.thePlayer.openGui(Wizardry.instance, GuiHandler.INDEX, mc.theWorld, (int) mc.thePlayer.posX, (int)
                        mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                currentPage = 0;
                break;
            }
            case 3: {
                if (bookmarkedPage == this) {
                    bookmarkedPage = null;
                    hasBookmark = false;
                } else {
                    bookmarkedPage = this;
                    hasBookmark = true;
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.setBidiFlag(true);

        int height = 0;
        if (pages.containsKey(currentPage)) {
            for (String line : pages.get(currentPage)) {
                fontRendererObj.drawString(line, left + 17, top + 13 + (height * 8), 0, false);
                height++;
            }
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect((width / 2) - 66, (float) (top - 20), 19, 182, 133, 14);
        fontRendererObj.setUnicodeFlag(false);
        fontRendererObj.setBidiFlag(false);
        fontRendererObj.drawString("Physics Book", (width / 2) - 30, (float) (top - 20) + 4, 0, false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
