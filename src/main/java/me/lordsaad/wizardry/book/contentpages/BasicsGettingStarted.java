package me.lordsaad.wizardry.book.contentpages;

import me.lordsaad.wizardry.Utils;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.book.Button;
import me.lordsaad.wizardry.book.GuiHandler;
import me.lordsaad.wizardry.book.Slot;
import me.lordsaad.wizardry.book.Tip;
import me.lordsaad.wizardry.schematic.BlockObject;
import me.lordsaad.wizardry.schematic.Schematic;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saad on 4/19/2016.
 */
public class BasicsGettingStarted extends ContentPageBase {

    HashMap<GuiButton, ResourceLocation> layerNavTextures = new HashMap<>();
    int layer = 0;
    int x = left + 15;
    int y = top + 150;

    @Override
    public void initGui() {
        super.initGui();
        String TEXT_RESOURCE = "/assets/wizardry/documentation/getting_started.txt";
        pages = Utils.splitTextToPages(pages, getClass().getResourceAsStream(TEXT_RESOURCE), this);
        pageID = GuiHandler.basics_getting_started;
        enableNavBar(true);

        Button UPLAYER;
        buttonList.add(UPLAYER = new Button(4, 0, 0, 15, 15));
        ResourceLocation upLayer = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/left_arrow.png");
        layerNavTextures.put(UPLAYER, upLayer);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (currentPage == 0) {
            HashMap<Slot, ItemStack> recipe = new HashMap<>();
            recipe.put(new Slot(0), new ItemStack(Items.DIAMOND));
            recipe.put(new Slot(1), new ItemStack(Items.DIAMOND));
            recipe.put(new Slot(2), new ItemStack(Items.DIAMOND));
            recipe.put(new Slot(4), new ItemStack(Items.STICK));
            recipe.put(new Slot(7), new ItemStack(Items.STICK));
            Tip tip = setTip("Snazzy.", new ItemStack(Items.DIAMOND_PICKAXE), recipe);
            if (tip != null) tipManager.put(currentPage, tip.getID());
        } else {
            if (tipManager.containsKey(currentPage)) {
                removeTip(tipManager.get(currentPage));
                tipManager.remove(currentPage);
            }
        }
        if (currentPage == 2) {
            Schematic schematic = new Schematic("spell_crafter");
            HashMap<Integer, ArrayList<BlockObject>> layers = schematic.getSchematicLayers();
            for (BlockObject object : layers.get(layer)) {
                Utils.drawItemStack(new ItemStack(object.getState().getBlock()), x + object.getPos().getX(), y + object.getPos().getZ());
            }
        }
    }
}

