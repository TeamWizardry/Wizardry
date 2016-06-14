package me.lordsaad.wizardry.book.contentpages;

import me.lordsaad.wizardry.Utils;
import me.lordsaad.wizardry.api.Constants;
import me.lordsaad.wizardry.book.Button;
import me.lordsaad.wizardry.book.Slot;
import me.lordsaad.wizardry.book.Tip;
import me.lordsaad.wizardry.schematic.BlockObject;
import me.lordsaad.wizardry.schematic.Schematic;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saad on 4/19/2016.
 */
public class BasicsGettingStarted extends ContentPageBase {

    int layer = 0;

    @Override
    public void initGui() {
        super.initGui();
        String TEXT_RESOURCE = "/assets/wizardry/documentation/getting_started.txt";
        pages = Utils.splitTextToPages(pages, getClass().getResourceAsStream(TEXT_RESOURCE), this);
        pageID = Constants.PageNumbers.BASICS_GETTING_STARTED;
        setNavBar(true);

        buttonList.add(new Button(Constants.GuiButtons.SCHEMATIC_UP_LAYER, 0, 0, 8, 19));
        buttonList.add(new Button(Constants.GuiButtons.SCHEMATIC_DOWN_LAYER, 0, 0, 8, 19));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        Schematic schematic = new Schematic("spell_crafter");
        switch (button.id) {
            case Constants.GuiButtons.SCHEMATIC_UP_LAYER: {
                if (schematic.getHeight() > layer)
                    layer++;
                break;
            }
            case Constants.GuiButtons.SCHEMATIC_DOWN_LAYER: {
                if (schematic.getHeight() < layer)
                    layer--;
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
            if (layers.containsKey(layer))
                for (BlockObject object : layers.get(layer)) {
                    if (object != null && object.getState() != null && object.getState().getBlock() != Blocks.AIR) {
                        ItemStack itemStack = new ItemStack(object.getState().getBlock(), 1);

                        GlStateManager.pushMatrix();
                        RenderHelper.enableGUIStandardItemLighting();
                        GlStateManager.translate(left + (bookBackgroundWidth / 2) - (schematic.getWidth() * 12 / 2) + object.getPos().getX() * 12,
                                top + (bookBackgroundWidth / 2) - (schematic.getWidth() * 12 / 2) + object.getPos().getZ() * 12, 1);
                        GlStateManager.scale(0.75, 0.75, 0.75);
                        itemRender.renderItemAndEffectIntoGUI(itemStack, 0, 0);

                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.popMatrix();

                        int x = left + (bookBackgroundWidth / 2) - (schematic.getWidth() * 12 / 2) + object.getPos().getX() * 12;
                        int y = top + (bookBackgroundWidth / 2) - (schematic.getWidth() * 12 / 2) + object.getPos().getZ() * 12;
                        int size = 13;
                        boolean insideBlock = mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size;
                        if (insideBlock) {
                            // TODO: tooltip goes null and crashes.
                            fontRendererObj.setBidiFlag(false);
                            fontRendererObj.setUnicodeFlag(false);
                            renderToolTip(itemStack, mouseX, mouseY);
                            fontRendererObj.setBidiFlag(true);
                            fontRendererObj.setUnicodeFlag(true);
                        }
                    }
                }

            for (GuiButton button : buttonList) {

                switch (button.id) {
                    case Constants.GuiButtons.SCHEMATIC_UP_LAYER: {
                        button.xPosition = left + (bookBackgroundWidth / 2) - (8 / 2) - 8;
                        button.yPosition = top + 135;
                        button.width = 8;
                        button.height = 19;
                        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
                        boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + button.width && mouseY >= button.yPosition && mouseY < button.yPosition + button.height;
                        if (inside) GlStateManager.color(0F, 153F, 0F, 1F);
                        else GlStateManager.color(0F, 128F, 255F, 1F);

                        if (inside) {
                            int tip = setTip(new Tip("Increment layer"));
                            if (tip != -1) tipManager.put(button, tip);
                        } else {
                            if (tipManager.containsKey(button)) {
                                removeTip(tipManager.get(button));
                                tipManager.remove(button);
                            }
                        }
                        drawTexturedModalRect(button.xPosition, button.yPosition, 0, 229, 9, 19);
                        break;
                    }
                    case Constants.GuiButtons.SCHEMATIC_DOWN_LAYER: {
                        button.xPosition = left + (bookBackgroundWidth / 2) - (8 / 2) + 8;
                        button.yPosition = top + 135;
                        button.width = 8;
                        button.height = 19;
                        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
                        boolean inside = mouseX >= button.xPosition && mouseX < button.xPosition + button.width && mouseY >= button.yPosition && mouseY < button.yPosition + button.height;
                        if (inside) GlStateManager.color(0F, 153F, 0F, 1F);
                        else GlStateManager.color(0F, 128F, 255F, 1F);

                        if (inside) {
                            int tip = setTip(new Tip("Increment layer"));
                            if (tip != -1) tipManager.put(button, tip);
                        } else {
                            if (tipManager.containsKey(button)) {
                                removeTip(tipManager.get(button));
                                tipManager.remove(button);
                            }
                        }
                        drawTexturedModalRect(button.xPosition, button.yPosition, 9, 229, 9, 19);
                    }
                }
            }
        }
    }
}

