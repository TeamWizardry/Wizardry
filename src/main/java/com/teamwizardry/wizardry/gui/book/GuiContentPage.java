package com.teamwizardry.wizardry.gui.book;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saad on 5/1/2016.
 */

/**
 * Extend this class to add actual text pages.
 */
public class GuiContentPage extends Tippable {

    protected static HashMap<Integer, ArrayList<String>> pages;
    protected static int currentPage = 0;
    protected boolean hasBookmark = PageBase.hasBookmark;
    protected int pageID;

    @Override
    public void initGui() {
        super.initGui();
        pages = new HashMap<>();
        pageID = 0;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case Constants.GuiButtons.NAV_BAR_BACK: {
                if (currentPage > 0) {
                    currentPage--;
                    mc.thePlayer.openGui(Wizardry.instance, pageID, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                } else {
                    mc.thePlayer.openGui(Wizardry.instance, Constants.PageNumbers.GUIDE, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                    currentPage = 0;
                }
                break;
            }
            case Constants.GuiButtons.NAV_BAR_NEXT: {
                if (pages.size() >= currentPage) {
                    currentPage++;
                    mc.thePlayer.openGui(Wizardry.instance, pageID, mc.theWorld, (int) mc.thePlayer.posX, (int)
                            mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                }
                break;
            }
            case Constants.GuiButtons.NAV_BAR_INDEX: {
                mc.thePlayer.openGui(Wizardry.instance, Constants.PageNumbers.GUIDE, mc.theWorld, (int) mc.thePlayer.posX, (int)
                        mc.thePlayer.posY, (int) mc.thePlayer.posZ);
                currentPage = 0;
                break;
            }
            case Constants.GuiButtons.BOOKMARK: {
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

        // set the text
        int height = 0;
        if (pages.containsKey(currentPage)) {
            for (String line : pages.get(currentPage)) {
                fontRendererObj.drawString(line, left + 17, top + 13 + (height * 8), 0, false);
                height++;
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
