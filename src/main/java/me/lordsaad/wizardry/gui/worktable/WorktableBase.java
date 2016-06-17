package me.lordsaad.wizardry.gui.worktable;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.spells.SpellIngredients;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableBase extends GuiScreen {

    public static int left, top, right;
    public static int backgroundWidth = 214, backgroundHeight = 220; // SIZE OF PAPER
    public static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sample-page-background.png");
    public static HashMap<SpellIngredients.IngredientType, ArrayList<Module>> modules;
    private Module moduleBeingDragged;
    private int iconSize = 16;
    private HashMap<SpellIngredients.IngredientType, ArrayList<Module>> modulesOnPaper;

    @Override
    public void initGui() {
        left = width / 2 - backgroundWidth / 2;
        top = height / 2 - backgroundHeight / 2;
        right = (width / 2 + backgroundWidth / 2) - 6;
        modules = new HashMap<>();
        modulesOnPaper = new HashMap<>();
        initModules();
    }

    private void initModules() {
        for (Class clazz : Modules.class.getDeclaredClasses()) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    Module module = (Module) field.get(clazz);
                    modules.putIfAbsent(module.getType(), new ArrayList<>());
                    ArrayList<Module> category = modules.get(module.getType());
                    category.add(module);
                    modules.put(module.getType(), category);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (SpellIngredients.IngredientType category : modules.keySet())
            for (Module module : modules.get(category)) {
                boolean inside = mouseX >= module.getX() && mouseX < module.getX() + iconSize && mouseY >= module.getY() && mouseY < module.getY() + iconSize;
                if (inside) {
                    moduleBeingDragged = module.copy();
                    moduleBeingDragged.setX(mouseX - iconSize / 2);
                    moduleBeingDragged.setY(mouseY - iconSize / 2);
                }
            }

        for (SpellIngredients.IngredientType category : modulesOnPaper.keySet()) {
            ArrayList<Module> tempModules = new ArrayList<>();
            tempModules.addAll(modulesOnPaper.get(category));

            for (Module module : modulesOnPaper.get(category)) {
                boolean inside = mouseX >= module.getX() - iconSize / 2 && mouseX < module.getX() - iconSize / 2 + iconSize && mouseY >= module.getY() - iconSize / 2 && mouseY < module.getY() - iconSize / 2 + iconSize;
                if (inside) {
                    moduleBeingDragged = module.copy();
                    moduleBeingDragged.setX(mouseX - iconSize / 2);
                    moduleBeingDragged.setY(mouseY - iconSize / 2);
                    tempModules.remove(module);
                }
            }
            modulesOnPaper.put(category, tempModules);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (moduleBeingDragged != null) {
            boolean inside = mouseX >= left && mouseX < left + backgroundWidth && mouseY >= top && mouseY < top + backgroundHeight;
            if (inside) {
                moduleBeingDragged.setX(mouseX);
                moduleBeingDragged.setY(mouseY);
                modulesOnPaper.putIfAbsent(moduleBeingDragged.getType(), new ArrayList<>());
                ArrayList<Module> category = modulesOnPaper.get(moduleBeingDragged.getType());
                category.add(moduleBeingDragged);
                modulesOnPaper.put(moduleBeingDragged.getType(), category);
                moduleBeingDragged = null;
            } else {
                moduleBeingDragged = null;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // RENDER BACKGROUND //
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(left, top, 0, 0, backgroundWidth, backgroundHeight);
        // RENDER BACKGROUND //

        int row = 0, column = 0, iconSeparation = 1;

        ArrayList<Module> tempModules = new ArrayList<>();
        tempModules.addAll(modules.get(SpellIngredients.IngredientType.EFFECT));

        for (Module module : modules.get(SpellIngredients.IngredientType.EFFECT)) {
            int x = left - 90 + iconSeparation + (row * iconSize) + (row * iconSeparation);
            int y = top + iconSeparation + (column * iconSize) + (column * iconSeparation);

            // Remove the module from the list so we can update it after we change the new x and y.
            tempModules.remove(module);

            module.setX(x);
            module.setY(y);

            // Updated module's x and y. Now add it back.
            tempModules.add(module);

            boolean inside = mouseX >= module.getX() && mouseX < module.getX() + iconSize && mouseY >= module.getY() && mouseY < module.getY() + iconSize;
            if (inside) {
                mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/blue-gradient.png"));
                drawScaledCustomSizeModalRect(module.getX() - iconSize / 2, module.getY() - iconSize / 2, 0, 0, iconSize * 2, iconSize * 2, iconSize * 2, iconSize * 2, iconSize * 2, iconSize * 2);
            }

            mc.renderEngine.bindTexture(module.getIcon());
            drawScaledCustomSizeModalRect(module.getX(), module.getY(), 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);


            if (row >= 3) {
                row = 0;
                column++;
            } else row++;
        }
        modules.remove(SpellIngredients.IngredientType.EFFECT);
        modules.put(SpellIngredients.IngredientType.EFFECT, tempModules);

        if (moduleBeingDragged != null) {
            moduleBeingDragged.setX(mouseX);
            moduleBeingDragged.setY(mouseY);
            mc.renderEngine.bindTexture(moduleBeingDragged.getIcon());
            drawScaledCustomSizeModalRect(moduleBeingDragged.getX() - iconSize / 2, moduleBeingDragged.getY() - iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
        }

        for (SpellIngredients.IngredientType type : modulesOnPaper.keySet()) {
            for (Module module : modulesOnPaper.get(type)) {
                mc.renderEngine.bindTexture(module.getIcon());
                drawScaledCustomSizeModalRect(module.getX() - iconSize / 2, module.getY() - iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

}
