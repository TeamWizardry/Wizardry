package me.lordsaad.wizardry.book.contentpages;

import me.lordsaad.wizardry.Utils;
import me.lordsaad.wizardry.book.GuiHandler;

/**
 * Created by Saad on 4/19/2016.
 */
public class BasicsGettingStarted extends ContentPageBase {

    @Override
    public void initGui() {
        super.initGui();
        String TEXT_RESOURCE = "/assets/wizardry/documentation/getting_started.txt";
        pages = Utils.splitTextToPages(pages, getClass().getResourceAsStream(TEXT_RESOURCE), this);
        pageID = GuiHandler.basics_getting_started;
    }
}

