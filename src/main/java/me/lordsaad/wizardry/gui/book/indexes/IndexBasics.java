package me.lordsaad.wizardry.gui.book.indexes;

import me.lordsaad.wizardry.ModItems;
import me.lordsaad.wizardry.api.Constants;
import me.lordsaad.wizardry.gui.book.Button;

/**
 * Created by Saad on 5/2/2016.
 */
public class IndexBasics extends GuiSubIndex {

    private int ID = 0;

    @Override
    public void initGui() {
        super.initGui();

        // GuiHandler page = 2
        Button gettingStarted = new Button(++ID, 0, 0, 10, 10);
        buttonList.add(gettingStarted);
        addElement(new SubIndexElement(ID, Constants.PageNumbers.BASICS_GETTING_STARTED, ModItems.physicsBook, "Getting started",
                "Learn the very basics of the mod and learn how and where to start.",
                gettingStarted));

        Button lightManipulation = new Button(++ID, 0, 0, 10, 10);
        buttonList.add(lightManipulation);
        addElement(new SubIndexElement(ID, 3, ModItems.pearl, "Light Manipulation",
                "Learn all the ins and outs of manipulating light and the many kinds of mirrors.",
                lightManipulation));
    }
}
