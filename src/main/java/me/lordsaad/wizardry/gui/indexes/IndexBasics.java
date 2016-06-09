package me.lordsaad.wizardry.gui.indexes;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.gui.Button;
import me.lordsaad.wizardry.gui.GuiHandler;
import me.lordsaad.wizardry.gui.GuiSubIndex;
import me.lordsaad.wizardry.gui.IndexItem;
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
        indexItems = new ArrayList<>();

        Button screwdriver = new Button(++ID, 0, 0, 10, 10);
        buttonList.add(screwdriver);
        indexItems.add(new IndexItem(ID,
                GuiHandler.basics_getting_started,
                new ResourceLocation(Wizardry.MODID, "textures/items/screwdriver.png"),
                "Getting started", "Learn the very basics of the mod and learn how and where to start.",
                screwdriver));
        Button mirror = new Button(++ID, 0, 0, 10, 10);
        buttonList.add(mirror);
        indexItems.add(new IndexItem(ID,
                GuiHandler.INDEX,
                new ResourceLocation(Wizardry.MODID, "textures/items/mirroricon.png"),
                "Light Manipulation", "Learn all the ins and outs of manipulating light and the many kinds of mirrors.",
                mirror));
    }
}
