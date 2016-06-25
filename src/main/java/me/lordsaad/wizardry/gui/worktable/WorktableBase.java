package me.lordsaad.wizardry.gui.worktable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.lordsaad.wizardry.Utils;
import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.Constants;
import me.lordsaad.wizardry.api.spells.SpellIngredients;
import me.lordsaad.wizardry.gui.book.Button;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableBase extends GuiScreen {

    private static int left, top, right;
    private static int backgroundWidth = 214, backgroundHeight = 220; // SIZE OF PAPER
    private static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sample-page-background.png");
    private static ArrayList<Module> modulesInSidebar;
    private ArrayList<Module> modulesOnPaper;
    private Multimap<Module, Module> links;
    private Module moduleBeingDragged, moduleBeingLinked;
    private int iconSize = 16;
    private int rotateShimmer = 0;

    @Override
    public void initGui() {
        left = width / 2 - backgroundWidth / 2;
        top = height / 2 - backgroundHeight / 2;
        right = (width / 2 + backgroundWidth / 2) - 6;
        modulesInSidebar = new ArrayList<>();
        modulesOnPaper = new ArrayList<>();
        links = HashMultimap.create();
        initModules();

        buttonList.add(new Button(Constants.WorkTable.DONE_BUTTON, backgroundWidth / 2 + 30, top + 100, 30, 30));
        buttonList.add(new Button(Constants.WorkTable.CONFIRM_BUTTON, backgroundWidth / 2 - 30, top + 100, 30, 30));
    }

    private void initModules() {
        int ID = 0;
        for (Class clazz : SpellIngredients.class.getDeclaredClasses()) {
            if (clazz != SpellIngredients.IngredientType.class)
                for (Field field : clazz.getDeclaredFields()) {
                    try {
                        Module module = ((Module) field.get(clazz)).copy();
                        module.setID(ID++);
                        module.setType(SpellIngredients.IngredientType.valueOf(clazz.getSimpleName().toUpperCase()));
                        modulesInSidebar.add(module);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

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
            } else {
                for (Module module : modulesOnPaper) {
                    if (links.get(moduleBeingDragged).contains(module))
                        links.get(moduleBeingDragged).remove(module);

                    if (links.get(module).contains(moduleBeingDragged))
                        links.get(module).remove(moduleBeingDragged);
                }
                if (modulesOnPaper.contains(moduleBeingDragged))
                    modulesOnPaper.remove(moduleBeingDragged);
                moduleBeingDragged = null;
            }
        }

        if (clickedMouseButton == 1) {
            if (moduleBeingLinked != null) {
                boolean insideAnything = false;
                for (Module module : modulesOnPaper) {
                    boolean inside = mouseX >= module.getX() - iconSize / 2 && mouseX < module.getX() - iconSize / 2 + iconSize && mouseY >= module.getY() - iconSize / 2 && mouseY < module.getY() - iconSize / 2 + iconSize;
                    if (inside) {
                        Module from = moduleBeingLinked;

                        boolean wasLinked = false;

                        if (links.get(from).contains(module)) {
                            links.get(from).remove(module);
                            wasLinked = true;
                        }
                        if (links.get(module).contains(from)) {
                            links.get(module).remove(from);
                            wasLinked = true;
                        }

                        if (!wasLinked) {
                            links.get(from).add(module);
                        }
                        moduleBeingLinked = null;
                        insideAnything = true;
                        break;
                    }
                }
                if (!insideAnything) moduleBeingLinked = null;
                
            } else moduleBeingLinked = null;
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

        // RENDER BUTTONS //
        for (GuiButton button : buttonList)
            if (button.id == Constants.WorkTable.CONFIRM_BUTTON) {
                mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/book/error.png"));
                drawScaledCustomSizeModalRect(button.xPosition, button.yPosition, 0, 0, 0, 0, 100, 50, 100, 50);
            } else if (button.id == Constants.WorkTable.DONE_BUTTON) {
                mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/book/fof.png"));
                drawScaledCustomSizeModalRect(button.xPosition, button.yPosition, 0, 0, 0, 0, 100, 50, 100, 50);
            }
        // RENDER BUTTONS //

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

        modulesOnPaper.stream().filter(module -> links.containsKey(module)).forEach(module -> {
            for (Module linkedModule : links.get(module))
                Utils.drawLine2D(module.getX(), module.getY(), linkedModule.getX(), linkedModule.getY(), 2, Color.BLACK);
        });
        // RENDER LINE BETWEEN LINKED MODULES //

        // RENDER SIDEBARS //
        int row = 0, column = 0, iconSeparation = 1, sidebarX;
        Module lastModule = null;
        for (Module module : modulesInSidebar) {

            // TODO: REDO THIS PART
            GlStateManager.color(1F, 1F, 1F, 1F);
            if (module.getType() == SpellIngredients.IngredientType.SPELLEFFECTS) sidebarX = -backgroundWidth / 2 - 90;
            else if (module.getType() == SpellIngredients.IngredientType.SPELLCONDITIONS)
                sidebarX = -backgroundWidth / 2 - 180;
            else if (module.getType() == SpellIngredients.IngredientType.SPELLEFFECTMODIFIERS || module.getType() == SpellIngredients.IngredientType.SPELLSHAPEMODIFIERS)
                sidebarX = backgroundWidth / 2 + 20;
            else if (module.getType() == SpellIngredients.IngredientType.SPELLEVENTS)
                sidebarX = backgroundWidth / 2 + 100;
            else sidebarX = backgroundWidth / 2 + 240;

            int x = width / 2 + sidebarX + iconSeparation + (column * iconSize) + (column * iconSeparation);
            int y = top + iconSeparation + (row * iconSize) + (row * iconSeparation);

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


            if (lastModule != null && lastModule.getType() == module.getType())
                if (column >= 3) {
                    row++;
                    column = 0;
                } else column++;
            else row = 0;
            lastModule = module;
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
