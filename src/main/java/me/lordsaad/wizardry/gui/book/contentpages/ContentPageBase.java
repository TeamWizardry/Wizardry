package me.lordsaad.wizardry.gui.book.contentpages;

import me.lordsaad.wizardry.gui.book.GuiContentPage;

/**
 * Created by Saad on 6/12/2016.
 */
public class ContentPageBase extends GuiContentPage {

    private int y = top + 50;

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (hasBookmark) {
            if (y >= top + 6) {
                y += (top + 50) - (top + 6);
                renderBookmark(y, true);
            } else renderBookmark(top + 6, true);
        } else renderBookmark(top + 6, false);
    }
}
