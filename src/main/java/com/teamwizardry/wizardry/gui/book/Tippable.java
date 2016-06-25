package com.teamwizardry.wizardry.gui.book;

import com.teamwizardry.wizardry.Utils;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Saad on 4/30/2016.
 */
public class Tippable extends PageBase {

    public static HashMap<Object, Integer> tipManager = new HashMap<>(); // Used to manage tips for pages. Runs solo
    static ArrayList<Integer> deleteTip = new ArrayList<>(); // Prevent concurrent modification error
    private static int IDs = 0;
    private static ResourceLocation SLIDERS = new ResourceLocation(Wizardry.MODID, "textures/gui/book/sliders.png");
    private static LinkedHashMap<Integer, Tip> tips = new LinkedHashMap<>();

    public static int setTip(Tip tip) {
        // Check if tip exists
        if (tip.getText() == null) tip.setText("<ERROR>");
        boolean tipAlreadyExists = false;
        for (int test : tips.keySet())
            if (tips.get(test).getText().equals(tip.getText())) {
                tipAlreadyExists = true;
                break;
            }

        // Save tip. Return null if it already exists
        if (!tipAlreadyExists) {
            tips.put(++IDs, tip);
            return IDs;
        } else return -1;
    }

    public static int setTip(String text) {
        if (text == null) text = "<ERROR>";
        Tip tip = new Tip(text);

        // Check if tip exists
        boolean tipAlreadyExists = false;
        for (int test : tips.keySet())
            if (tips.get(test).getText().equals(text)) {
                tipAlreadyExists = true;
                break;
            }

        // Save tip. Return null if it already exists
        if (!tipAlreadyExists) {
            tips.put(++IDs, tip);
            return IDs;
        } else return 0;
    }

    public static int setTip(String text, ItemStack recipeOutput, HashMap<Slot, ItemStack> recipe) {
        if (text == null) text = "<ERROR>";
        Tip tip = new Tip(text, recipeOutput, recipe);

        // Check if tip exists
        boolean tipAlreadyExists = false;
        for (int test : tips.keySet())
            if (tips.get(test).getText().equals(text)) {
                tipAlreadyExists = true;
                break;
            }

        // Save tip. Return null if it already exists
        if (!tipAlreadyExists) {
            tips.put(++IDs, tip);
            return IDs;
        } else return 0;
    }

    static void clearTips() {
        IDs = -1;
        tips.clear();
        tipManager.clear();
    }

    static void retractAllTips() {
        for (Integer ID : tips.keySet()) {
            tips.get(ID).setSlidingOut(false);
        }
        tipManager.clear();
    }

    public static void removeTip(int ID) {
        if (tips.containsKey(ID)) tips.get(ID).setSlidingOut(false);
    }

    @Override
    public void initGui() {
        super.initGui();
        retractAllTips();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        fontRendererObj.setUnicodeFlag(true);
        fontRendererObj.setBidiFlag(true);

        // Remove finished tips
        for (int ID : deleteTip)
            tips.remove(ID);
        deleteTip.clear();

        // Draw
        for (int ID : tips.keySet()) {
            Tip tip = tips.get(ID);

            // Calculate x for each tip //
            float x = tip.getX();
            if (tip.isSlidingOut()) {
                if (x >= -144) x -= (145 - Math.abs(x)) / 3; // Slide out
            } else if (x <= -5)
                if (((int) (145 - Math.abs(x)) / 2 != x) && (((int) (145 - Math.abs(x) + x)) <= 0))
                    x += (145 - Math.abs(x)) / 2; // Slide in
                else tip.setComplete(true, ID);
            else tip.setComplete(true, ID);

            if (x <= -144 && ID != ID + 1)
                if (tips.containsKey(ID + 1))
                    tip.setSlidingOut(false);

            tip.setX(x);

            // RENDER TEXT TIPS //
            // render slider
            GlStateManager.color(1F, 1F, 1F, 1F);
            mc.renderEngine.bindTexture(SLIDERS);
            drawTexturedModalRect(left + x + 17, tip.getY(), 0, 0, 133, 37);

            // render text
            ArrayList<String> lines = Utils.padString(tip.getText(), 31);
            for (String line : lines)
                fontRendererObj.drawString(line.trim(), left + x + 22, (tip.getY() + 3) + lines.indexOf(line) * 8, 0, false);
            // RENDER TEXT TIPS //

            // RENDER RECIPE TIPS //
            if (tip.hasRecipe()) {
                fontRendererObj.setUnicodeFlag(false);
                fontRendererObj.setBidiFlag(false);
                // render slider
                GlStateManager.color(1F, 1F, 1F, 1F);
                mc.renderEngine.bindTexture(SLIDERS);
                drawTexturedModalRect(left + x + 17, tip.getY(), 0, 37, 133, 68);

                // render recipe output item
                if (tip.isSlidingOut() && ID == IDs) {

                    int outputX = (int) (left + x + 117), outputY = (int) (tip.getY()) + 26;
                    int size = 17;
                    Utils.drawNormalItemStack(tip.getRecipeOutput(), outputX, outputY);
                    boolean inside = mouseX >= outputX && mouseX < outputX + size && mouseY >= outputY && mouseY < outputY + size;
                    if (inside) renderToolTip(tip.getRecipeOutput(), (int) (left + x + 10), top + 94);

                    // render recipe items
                    for (Slot slot : tip.getRecipe().keySet()) {
                        int deltaX = (int) (left + x + 26 + slot.getX() * 18);
                        int deltaY = (int) (tip.getY()) + 8 + slot.getY() * 18;
                        Utils.drawNormalItemStack(tip.getRecipe().get(slot), deltaX, deltaY);
                        boolean insideSlot = mouseX >= deltaX && mouseX < deltaX + size && mouseY >= deltaY && mouseY < deltaY + size;
                        if (insideSlot)
                            renderToolTip(tip.getRecipe().get(slot), (int) (left + x + 10), top + 94);
                    }
                }
            }
            // RENDER RECIPE TIPS //
        }

        fontRendererObj.setBidiFlag(false);
        fontRendererObj.setUnicodeFlag(false);

        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(left, top, 0, 0, bookBackgroundWidth, bookBackgroundHeight);
    }
}
