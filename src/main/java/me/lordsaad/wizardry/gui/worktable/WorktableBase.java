package me.lordsaad.wizardry.gui.worktable;

import me.lordsaad.wizardry.Utils;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.Constants;
import me.lordsaad.wizardry.api.spells.SpellIngredients;
import me.lordsaad.wizardry.gui.book.Button;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static me.lordsaad.wizardry.api.spells.SpellIngredients.IngredientType.*;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableBase extends GuiScreen {

    public static int left, top, right;
    public static int backgroundWidth = 214, backgroundHeight = 220; // SIZE OF PAPER
    public static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sample-page-background.png");
    public static HashMap<SpellIngredients.IngredientType, ArrayList<Module>> modulesInSidebar;
    private ArrayList<Module> modulesOnPaper;
    //private HashMap<SpellIngredients.IngredientType, ArrayList<Module>> modulesOnPaper;
    // private HashMap<Module, Module> modulesLinked;
    private Module moduleBeingDragged, moduleBeingLinked;
    private int iconSize = 16;
    private boolean linkingMode = false;
    private int rotateShimmer = 0;

    @Override
    public void initGui() {
        left = width / 2 - backgroundWidth / 2;
        top = height / 2 - backgroundHeight / 2;
        right = (width / 2 + backgroundWidth / 2) - 6;
        modulesInSidebar = new HashMap<>();
        modulesOnPaper = new ArrayList<>();
        //  modulesLinked = new HashMap<>();
        buttonList.add(new Button(Constants.WorkTable.LINKING_TOOL, 0, 0, iconSize, iconSize));
        initModules();
    }

    private void initModules() {
        for (Class clazz : Modules.class.getDeclaredClasses()) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    Module module = (Module) field.get(clazz);
                    modulesInSidebar.putIfAbsent(module.getType(), new ArrayList<>());
                    ArrayList<Module> category = modulesInSidebar.get(module.getType());
                    category.add(module);
                    modulesInSidebar.put(module.getType(), category);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // Get a module from the sidebar.
        for (SpellIngredients.IngredientType category : modulesInSidebar.keySet())
            for (Module module : modulesInSidebar.get(category)) {
                boolean inside = mouseX >= module.getX() && mouseX < module.getX() + iconSize && mouseY >= module.getY() && mouseY < module.getY() + iconSize;
                if (inside) {
                    moduleBeingDragged = module.copy();
                    moduleBeingDragged.setX(mouseX - iconSize / 2);
                    moduleBeingDragged.setY(mouseY - iconSize / 2);
                }
            }

        // Drag/Readjust module on paper.

        // Prevent concurrent modification
        ArrayList<Module> tempModules = new ArrayList<>();
        tempModules.addAll(modulesOnPaper);
        boolean insideAnything = false;

        for (Module module : modulesOnPaper) {
            boolean inside = mouseX >= module.getX() - iconSize / 2 && mouseX < module.getX() - iconSize / 2 + iconSize && mouseY >= module.getY() - iconSize / 2 && mouseY < module.getY() - iconSize / 2 + iconSize;
            if (inside) {
                insideAnything = true;
                if (!linkingMode) {
                    moduleBeingDragged = module.copy();
                    moduleBeingDragged.setX(mouseX);
                    moduleBeingDragged.setY(mouseY);
                    tempModules.remove(module);
                    break;
                }
            } else insideAnything = false;
        }

        if (!insideAnything && linkingMode) linkingMode = false;

        modulesOnPaper.clear();
        modulesOnPaper.addAll(tempModules);

        // Linking mode button
        int x = left + backgroundWidth / 2 - 32 / 2, y = top - 20;
        if (mouseX >= x && mouseX < x + 32 && mouseY >= y && mouseY < y + 32) linkingMode = true;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (linkingMode && moduleBeingLinked == null)
            for (Module module : modulesOnPaper) {
                boolean inside = mouseX >= module.getX() - iconSize / 2 && mouseX < module.getX() - iconSize / 2 + iconSize && mouseY >= module.getY() - iconSize / 2 && mouseY < module.getY() - iconSize / 2 + iconSize;
                if (inside) {
                    moduleBeingLinked = module;
                    break;
                }
            }

        if (!linkingMode && moduleBeingDragged != null)
            if (!moduleBeingDragged.getModules().isEmpty()) {
                ArrayList<Module> tempModules = new ArrayList<>();
                for (Module module : moduleBeingDragged.getModules())
                    if (module.getModules().contains(moduleBeingDragged)) {
                        module.getModules().remove(moduleBeingDragged);
                        module.getModules().add(moduleBeingDragged);
                        tempModules.add(module);
                    }
                moduleBeingDragged.getModules().clear();
                moduleBeingDragged.getModules().addAll(tempModules);
            }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (moduleBeingDragged != null) {
            boolean inside = mouseX >= left && mouseX < left + backgroundWidth && mouseY >= top && mouseY < top + backgroundHeight;
            if (inside) {
                moduleBeingDragged.setX(mouseX);
                moduleBeingDragged.setY(mouseY);
                modulesOnPaper.add(moduleBeingDragged);
                moduleBeingDragged = null;
            } else {
                moduleBeingDragged.getModules().clear();
                moduleBeingDragged = null;
            }
        } else if (linkingMode && moduleBeingLinked != null) {
            boolean insideAnything = false;
            for (Module module : modulesOnPaper) {
                boolean inside = mouseX >= module.getX() - iconSize / 2 && mouseX < module.getX() - iconSize / 2 + iconSize && mouseY >= module.getY() - iconSize / 2 && mouseY < module.getY() - iconSize / 2 + iconSize;
                if (inside) {
                    module.getModules().add(moduleBeingLinked);
                    moduleBeingLinked.getModules().add(module);
                    moduleBeingLinked = null;
                    linkingMode = false;
                    insideAnything = true;
                    break;
                } else insideAnything = false;
            }

            if (!insideAnything) {
                linkingMode = false;
                moduleBeingLinked = null;
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

        // RENDER LINKING BUTTON //
        mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/book/404.png"));
        drawScaledCustomSizeModalRect(left + backgroundWidth / 2 - 32 / 2, top - 20, 0, 0, 32, 32, 32, 32, 32, 32);
        // RENDER LINKING BUTTON //

        // SHIMMER CURSOR IF LINKING MODE //
        if (linkingMode) {
            GlStateManager.pushMatrix();
            if (rotateShimmer < 360) rotateShimmer++;
            else rotateShimmer = 0;
            GlStateManager.translate(mouseX, mouseY, 0);
            GlStateManager.rotate(rotateShimmer * 5, 0, 0, 1);
            GlStateManager.translate(-mouseX, -mouseY, 0);
            mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/shimmer.png"));
            drawScaledCustomSizeModalRect(mouseX - 16 / 2, mouseY - 16 / 2, 0, 0, 16, 16, 16, 16, 16, 16);
            GlStateManager.popMatrix();
        }
        // SHIMMER CURSOR IF LINKING MODE //

        // RENDER LINE BETWEEN LINKED MODULES //
        if (moduleBeingDragged != null) if (!moduleBeingDragged.getModules().isEmpty())
            for (Module module : moduleBeingDragged.getModules())
                Utils.drawLine2D(moduleBeingDragged.getX(), moduleBeingDragged.getY(), module.getX(), module.getY(), 2, Color.BLACK);
        if (moduleBeingLinked != null)
            Utils.drawLine2D(moduleBeingLinked.getX(), moduleBeingLinked.getY(), mouseX, mouseY, 2, Color.BLACK);
        modulesOnPaper.stream().filter(module -> !module.getModules().isEmpty()).forEach(module -> {
            for (Module linkedModule : module.getModules())
                Utils.drawLine2D(module.getX(), module.getY(), linkedModule.getX(), linkedModule.getY(), 2, Color.BLACK);
        });
        // RENDER LINE BETWEEN LINKED MODULES //

        // RENDER SIDEBARS //
        for (SpellIngredients.IngredientType category : modulesInSidebar.keySet()) {

            // CATEGORIES //
            int row = 0, column = 0, iconSeparation = 1, sidebarX;

            if (category == EFFECT) sidebarX = -backgroundWidth / 2 - 90;
            else if (category == CONDITION) sidebarX = -backgroundWidth / 2 - 180;
            else if (category == MODIFIER) sidebarX = backgroundWidth / 2 + 20;
            else if (category == EVENT) sidebarX = backgroundWidth / 2 + 100;
            else sidebarX = right + 240; // PERSPECTIVE

            // CATEGORY HEADER //
            fontRendererObj.setUnicodeFlag(true);
            fontRendererObj.setBidiFlag(true);
            fontRendererObj.drawString(category.getName(), sidebarX / 2, top, Color.WHITE.getRGB());
            fontRendererObj.setUnicodeFlag(false);
            fontRendererObj.setBidiFlag(false);
            // CATEGORY HEADER //


            ArrayList<Module> tempModules = new ArrayList<>();
            tempModules.addAll(modulesInSidebar.get(category));

            for (Module module : modulesInSidebar.get(category)) {
                int x = width / 2 + sidebarX + iconSeparation + (row * iconSize) + (row * iconSeparation);
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
            modulesInSidebar.put(category, tempModules);
        }
        // RENDER SIDEBARS //

        // RENDER MODULE ON CURSOR //
        if (moduleBeingDragged != null) {
            moduleBeingDragged.setX(mouseX);
            moduleBeingDragged.setY(mouseY);
            mc.renderEngine.bindTexture(moduleBeingDragged.getIcon());
            drawScaledCustomSizeModalRect(moduleBeingDragged.getX() - iconSize / 2, moduleBeingDragged.getY() - iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
        }
        // RENDER MODULE ON CURSOR //

        // RENDER MODULE ON THE PAPER //
        for (Module module : modulesOnPaper) {
            mc.renderEngine.bindTexture(module.getIcon());
            drawScaledCustomSizeModalRect(module.getX() - iconSize / 2, module.getY() - iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
        }
        // RENDER MODULE ON THE PAPER //
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

}
