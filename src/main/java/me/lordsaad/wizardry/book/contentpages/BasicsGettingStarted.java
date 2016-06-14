package me.lordsaad.wizardry.book.contentpages;

import me.lordsaad.wizardry.Utils;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.Constants;
import me.lordsaad.wizardry.book.Button;
import me.lordsaad.wizardry.book.Slot;
import me.lordsaad.wizardry.schematic.BlockObject;
import me.lordsaad.wizardry.schematic.Schematic;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saad on 4/19/2016.
 */
public class BasicsGettingStarted extends ContentPageBase {

    HashMap<GuiButton, ResourceLocation> layerNavTextures = new HashMap<>();
    int layer = 0;

    @Override
    public void initGui() {
        super.initGui();
        String TEXT_RESOURCE = "/assets/wizardry/documentation/getting_started.txt";
        pages = Utils.splitTextToPages(pages, getClass().getResourceAsStream(TEXT_RESOURCE), this);
        pageID = Constants.PageNumbers.BASICS_GETTING_STARTED;
        setNavBar(true);

        Button UPLAYER;
        buttonList.add(UPLAYER = new Button(4, 0, 0, 15, 15));
        ResourceLocation upLayer = new ResourceLocation(Wizardry.MODID, "textures/book/navbaricons/left_arrow.png");
        layerNavTextures.put(UPLAYER, upLayer);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 4: {
                layer++;
                break;
            }
        }
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
            int tip = setTip("Snazzy.", new ItemStack(Items.DIAMOND_PICKAXE), recipe);
            if (tip != -1) tipManager.put(currentPage, tip);
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
                if (object != null) {
                    Utils.drawSmallItemStack(new ItemStack(object.getState().getBlock()), left + 118 + object.getPos().getX() * 15, top + 50 + object.getPos().getZ() * 15);
                    int x = left + 22 + object.getPos().getX() * 12;
                    int y = top + 15 + object.getPos().getZ() * 12;
                    int size = 13;
                    boolean insideBlock = mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size;
                    if (insideBlock) renderToolTip(new ItemStack(object.getState().getBlock()), mouseX, mouseY);
                }
            }

            for (GuiButton button : layerNavTextures.keySet()) {

                int x = left + 22;
                int y = top + 150;

                button.xPosition = x;
                button.yPosition = y;
                button.width = 15;
                button.height = 15;
                mc.renderEngine.bindTexture(layerNavTextures.get(button));
                boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + button.width && mouseY >= button.yPosition && mouseY < button.yPosition + button.height;
                if (inside) GlStateManager.color(20F, 100F, 135F, 1F);
                else GlStateManager.color(0F, 170F, 255F, 1F);

                if (inside) {
                    int tip = setTip("Increment layer");
                    if (tip != -1) tipManager.put(button, tip);
                    GlStateManager.color(0F, 191F, 255F, 1F);
                } else {
                    if (tipManager.containsKey(button)) {
                        removeTip(tipManager.get(button));
                        tipManager.remove(button);
                    }
                    GlStateManager.color(0F, 0F, 0F, 1F);
                }

                drawScaledCustomSizeModalRect(x, y, 0, 0, 6, 10, 6, 10, 6, 10);
                GlStateManager.color(1F, 1F, 1F, 1F);
            }
        }
    }
}

