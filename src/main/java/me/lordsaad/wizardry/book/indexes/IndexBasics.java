package me.lordsaad.wizardry.book.indexes;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.book.Button;
import me.lordsaad.wizardry.book.GuiHandler;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

/**
 * Created by Saad on 5/2/2016.
 */
public class IndexBasics extends GuiSubIndex {

    private int ID = 3;

    @Override
    public void initGui() {
        super.initGui();
        SubIndexItems = new ArrayList<>();

        Button gettingStarted = new Button(++ID, 0, 0, 10, 10);
        buttonList.add(gettingStarted);
        SubIndexItems.add(new SubIndexItem(ID,
                GuiHandler.basics_getting_started,
                new ResourceLocation(Wizardry.MODID, "textures/items/physics-book.png"),
                "Getting started", "Learn the very basics of the mod and learn how and where to start.",
                gettingStarted));
        Button lightManipulation = new Button(++ID, 0, 0, 10, 10);
        buttonList.add(lightManipulation);
        SubIndexItems.add(new SubIndexItem(ID,
                GuiHandler.INDEX,
                new ResourceLocation(Wizardry.MODID, "textures/items/physics-book.png"),
                "Light Manipulation", "Learn all the ins and outs of manipulating light and the many kinds of mirrors.",
                lightManipulation));
    }
}
