package me.lordsaad.wizardry.book.pages;

import me.lordsaad.wizardry.Utils;
import me.lordsaad.wizardry.book.GuiContentPage;
import me.lordsaad.wizardry.book.GuiHandler;

/**
 * Created by Saad on 4/19/2016.
 */
public class GettingStarted extends GuiContentPage {

    int y = top + 50;

    @Override
    public void initGui() {
        super.initGui();
        String TEXT_RESOURCE = "/assets/wizardry/documentation/getting_started.txt";
        pages = Utils.splitTextToPages(pages, getClass().getResourceAsStream(TEXT_RESOURCE), this);
        pageID = GuiHandler.basics_getting_started;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (hasBookmark) {
            if (y >= top + 6) {
                y += (top + 50) - (top + 6);
                renderBookmark(y, true);
            } else renderBookmark(top + 6, true);
        } else {
            renderBookmark(top + 6, false);
        }
    }
}

