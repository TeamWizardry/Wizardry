package me.lordsaad.wizardry.gui.worktable;

import me.lordsaad.wizardry.Utils;
import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static me.lordsaad.wizardry.api.spells.SpellIngredients.IngredientType.*;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableBase extends GuiScreen {

    public static int left, top, right;
    public static int backgroundWidth = 214, backgroundHeight = 220; // SIZE OF PAPER
    public static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sample-page-background.png");
    public static ArrayList<Module> modulesInSidebar;
    private ArrayList<Module> modulesOnPaper;
    private HashMap<Module, HashSet<Module>> links;
    private Module moduleBeingDragged, moduleBeingLinked;
    private int iconSize = 16;
    //  private boolean linkingMode = false;
    private int rotateShimmer = 0;

    @Override
    public void initGui() {
        left = width / 2 - backgroundWidth / 2;
        top = height / 2 - backgroundHeight / 2;
        right = (width / 2 + backgroundWidth / 2) - 6;
        modulesInSidebar = new ArrayList<>();
        modulesOnPaper = new ArrayList<>();
        links = new HashMap<>();
        initModules();
    }

    private void initModules() {
        int ID = 0;
        for (Class clazz : Modules.class.getDeclaredClasses()) {
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    Module module = ((Module) field.get(clazz)).copy();
                    module.setID(ID++);
                    modulesInSidebar.add(module);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

   /* private void updateModuleLocation(Module module) {
        if (module == null) return;
        if (module.getModules().isEmpty()) return;

        boolean inside = module.getX() >= left && module.getX() < left + backgroundWidth && module.getY() >= top && module.getY() < top + backgroundHeight;

        ArrayList<Module> concurrentModules = new ArrayList<>();
        concurrentModules.addAll(module.getModules()); // original
        for (Module linkedModule : concurrentModules) { // linked to original

            ArrayList<Module> doubleConcurrentModules = new ArrayList<>();
            doubleConcurrentModules.addAll(linkedModule.getModules());
            linkedModule.getModules().stream().filter(doubleLinkedModule -> doubleLinkedModule == module).filter(doubleLinkedModule -> doubleLinkedModule.getX() != module.getX() || doubleLinkedModule.getY() != module.getY()).forEach(doubleLinkedModule -> {
                if (inside) {
                    doubleConcurrentModules.remove(doubleLinkedModule);
                    doubleConcurrentModules.add(module);
                } else doubleConcurrentModules.remove(doubleLinkedModule);
            });
            linkedModule.setModules(doubleConcurrentModules);
        }
        module.setModules(concurrentModules);
    }*/

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int clickedMouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedMouseButton);

        // Get a module from the sidebar.
        for (Module module : modulesInSidebar) {
            boolean inside = mouseX >= module.getX() && mouseX < module.getX() + iconSize && mouseY >= module.getY() && mouseY < module.getY() + iconSize;
            if (inside) {
                moduleBeingDragged = module.copy();
                moduleBeingDragged.setX(mouseX - iconSize / 2);
                moduleBeingDragged.setY(mouseY - iconSize / 2);
            }
        }

        // Drag/Readjust module on paper.
        for (Module module : modulesOnPaper) {
            boolean inside = mouseX >= module.getX() - iconSize / 2 && mouseX < module.getX() - iconSize / 2 + iconSize && mouseY >= module.getY() - iconSize / 2 && mouseY < module.getY() - iconSize / 2 + iconSize;
            if (inside && clickedMouseButton == 0) {
                moduleBeingDragged = module;
                moduleBeingDragged.setX(mouseX);
                moduleBeingDragged.setY(mouseY);
                break;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (moduleBeingLinked == null && clickedMouseButton == 1)
            for (Module module : modulesOnPaper) {
                boolean inside = mouseX >= module.getX() - iconSize / 2 && mouseX < module.getX() - iconSize / 2 + iconSize && mouseY >= module.getY() - iconSize / 2 && mouseY < module.getY() - iconSize / 2 + iconSize;
                if (inside) {
                    moduleBeingLinked = module;
                    break;
                }
            }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int clickedMouseButton) {
        if (clickedMouseButton == 0 && moduleBeingDragged != null) {
            boolean inside = mouseX >= left && mouseX < left + backgroundWidth && mouseY >= top && mouseY < top + backgroundHeight;
            if (inside) {
                moduleBeingDragged.setX(mouseX);
                moduleBeingDragged.setY(mouseY);
                modulesOnPaper.add(moduleBeingDragged);
                moduleBeingDragged = null;
            } else moduleBeingDragged = null;
        }

        if (clickedMouseButton == 1 && moduleBeingLinked != null) {
            boolean insideAnything = false;
            for (Module module : modulesOnPaper) {
                boolean inside = mouseX >= module.getX() - iconSize / 2 && mouseX < module.getX() - iconSize / 2 + iconSize && mouseY >= module.getY() - iconSize / 2 && mouseY < module.getY() - iconSize / 2 + iconSize;
                if (inside) {
                    HashSet<Module> temp;
                    if (links.containsKey(moduleBeingLinked)) temp = links.get(moduleBeingLinked);
                    else temp = new HashSet<>();
                    temp.add(module);
                    links.putIfAbsent(moduleBeingLinked, temp);
                    moduleBeingLinked = null;
                    insideAnything = true;
                    break;
                } else insideAnything = false;
            }

            if (!insideAnything) moduleBeingLinked = null;
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

        // SHIMMER CURSOR IF LINKING MODE //
        if (moduleBeingLinked != null) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.color(1F, 1F, 1F, 1F);
            if (rotateShimmer < 360) rotateShimmer++;
            else rotateShimmer = 0;
            GlStateManager.translate(mouseX, mouseY, 0);
            GlStateManager.rotate(rotateShimmer * 5, 0, 0, 1);
            GlStateManager.translate(-mouseX, -mouseY, 0);
            mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/shimmer.png"));
            drawScaledCustomSizeModalRect(mouseX - 16 / 2, mouseY - 16 / 2, 0, 0, 16, 16, 16, 16, 16, 16);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        // SHIMMER CURSOR IF LINKING MODE //

        // RENDER LINE BETWEEN LINKED MODULES //
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (moduleBeingDragged != null)
            if (links.containsKey(moduleBeingDragged))
                for (Module linkedModule : links.get(moduleBeingDragged))
                    Utils.drawLine2D(moduleBeingDragged.getX(), moduleBeingDragged.getY(), linkedModule.getX(), linkedModule.getY(), 2, Color.BLACK);

        if (moduleBeingLinked != null)
            Utils.drawLine2D(moduleBeingLinked.getX(), moduleBeingLinked.getY(), mouseX, mouseY, 2, Color.BLACK);

        for (Module module : modulesOnPaper) {
            if (links.containsKey(module)) {
                for (Module linkedModule : links.get(module))
                    Utils.drawLine2D(module.getX(), module.getY(), linkedModule.getX(), linkedModule.getY(), 2, Color.BLACK);
            }
        }
        // RENDER LINE BETWEEN LINKED MODULES //

        // RENDER SIDEBARS //
        int row = 0, column = 0, iconSeparation = 1, sidebarX;
        for (Module module : modulesInSidebar) {

            GlStateManager.color(1F, 1F, 1F, 1F);
            if (module.getType() == EFFECT) sidebarX = -backgroundWidth / 2 - 90;
            else if (module.getType() == CONDITION) sidebarX = -backgroundWidth / 2 - 180;
            else if (module.getType() == MODIFIER) sidebarX = backgroundWidth / 2 + 20;
            else if (module.getType() == EVENT) sidebarX = backgroundWidth / 2 + 100;
            else sidebarX = right + 240; // PERSPECTIVE

            int x = width / 2 + sidebarX + iconSeparation + (row * iconSize) + (row * iconSeparation);
            int y = top + iconSeparation + (column * iconSize) + (column * iconSeparation);

            module.setX(x);
            module.setY(y);

            // Highlight
            boolean inside = mouseX >= module.getX() && mouseX < module.getX() + iconSize && mouseY >= module.getY() && mouseY < module.getY() + iconSize;
            if (inside) {
                mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/blue-gradient.png"));
                drawScaledCustomSizeModalRect(module.getX() - iconSize / 2, module.getY() - iconSize / 2, 0, 0, iconSize * 2, iconSize * 2, iconSize * 2, iconSize * 2, iconSize * 2, iconSize * 2);
            }

            // Render the actual icon
            mc.renderEngine.bindTexture(module.getIcon());
            drawScaledCustomSizeModalRect(module.getX(), module.getY(), 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);


            if (row >= 3) {
                row = 0;
                column++;
            } else row++;
        }
        // RENDER SIDEBARS //

        // RENDER MODULE ON CURSOR //
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (moduleBeingDragged != null) {
            moduleBeingDragged.setX(mouseX);
            moduleBeingDragged.setY(mouseY);
            mc.renderEngine.bindTexture(moduleBeingDragged.getIcon());
            drawScaledCustomSizeModalRect(moduleBeingDragged.getX() - iconSize / 2, moduleBeingDragged.getY() - iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
        }
        // RENDER MODULE ON CURSOR //

        // RENDER MODULE ON THE PAPER //
        GlStateManager.color(1F, 1F, 1F, 1F);
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
