package com.teamwizardry.wizardry.client.gui.worktable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.api.util.misc.Utils;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.client.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableGui extends GuiScreen {

    private static int left, top, paperLeft = 160, paperTop = 0;
    private static int backgroundWidth = 512, backgroundHeight = 256, paperWidth = 191, paperHeight = 202;
    private static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/table_background.png");
    private HashMap<ModuleType, ArrayList<WorktableModule>> moduleCategories;
    private ArrayList<WorktableModule> modulesInSidebar;
    private ArrayList<WorktableModule> modulesOnPaper;
    private Multimap<WorktableModule, WorktableModule> links;
    private WorktableModule moduleBeingDragged, moduleBeingLinked, masterModule, moduleSelected;
    private int iconSize = 12;
    private int rotateShimmer = 0;
    private Texture spriteSheet = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sprite_sheet.png"), 256, 256);

    @Override
    public void initGui() {
        left = width / 2 - backgroundWidth / 2;
        top = height / 2 - backgroundHeight / 2;

        moduleCategories = new HashMap<>();
        modulesInSidebar = new ArrayList<>();
        modulesOnPaper = new ArrayList<>();
        links = HashMultimap.create();

        initModules();

        // TODO: move worktable to component based buttons
        //buttonList.add(new Button(Constants.WorkTable.DONE_BUTTON, backgroundWidth / 2 + 30, top + 100, 30, 30));
        //buttonList.add(new Button(Constants.WorkTable.CONFIRM_BUTTON, backgroundWidth / 2 - 30, top + 100, 30, 30));
    }

    private void initModules() {
        // Construct the new module
        for (ModuleList.IModuleConstructor moduleConstructor : ModuleList.INSTANCE.modules.values()) {
            // Construct a new module object
            Module module = moduleConstructor.construct();
            //module.setIcon(new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));

            // Add it into moduleCategories
            moduleCategories.putIfAbsent(module.getType(), new ArrayList<>());
            ArrayList<WorktableModule> modules = moduleCategories.get(module.getType());
            modules.add(new WorktableModule(module));
            moduleCategories.put(module.getType(), modules);

            // Add it into modulesInSiderbar
            modulesInSidebar.add(new WorktableModule(module));
        }

        // Recalculate module positions to their respective sidebars
        HashMap<ModuleType, ArrayList<WorktableModule>> copyModuleCategories = new HashMap<>();
        for (ModuleType type : moduleCategories.keySet()) {

            // Calculate where the sidebar is
            int row = 0, rowMax = 2, columnMax = 2, column = 0, sidebarLeft = 0, sidebarTop = 0;
            switch (type) {
                case BOOLEAN:
                    sidebarLeft = left + 39;
                    sidebarTop = top + 123;
                    columnMax = 5;
                    rowMax = 2;
                    break;
                case SHAPE:
                    sidebarLeft = left + 39;
                    sidebarTop = top + 39;
                    columnMax = 5;
                    rowMax = 2;
                    break;
                case EVENT:
                    sidebarLeft = left + 375;
                    sidebarTop = top + 38;
                    rowMax = 3;
                    columnMax = 6;
                    break;
                case EFFECT:
                    sidebarLeft = left + 87;
                    sidebarTop = top + 39;
                    rowMax = 3;
                    columnMax = 12;
                    break;
                case MODIFIER:
                    sidebarLeft = left + 435;
                    sidebarTop = top + 38;
                    rowMax = 3;
                    columnMax = 6;
            }

            // Add the actual module into the calculated sidebar positions
            for (WorktableModule module : moduleCategories.get(type)) {

                int iconSeparation = 0;
                int x = sidebarLeft + (row * iconSize) + (row * iconSeparation);
                int y = sidebarTop + (column * iconSize) + (column * iconSeparation);

                module.setX(x);
                module.setY(y);

                if (row >= rowMax - 1) {
                    row = 0;
                    if (column < columnMax) column++;
                } else row++;

                copyModuleCategories.putIfAbsent(type, new ArrayList<>());
                ArrayList<WorktableModule> modules = copyModuleCategories.get(type);
                modules.add(module);
                copyModuleCategories.put(type, modules);
            }
        }
        moduleCategories.clear();
        moduleCategories.putAll(copyModuleCategories);
        modulesInSidebar.clear();
        for (ArrayList<WorktableModule> modules : moduleCategories.values()) modulesInSidebar.addAll(modules);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int clickedMouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, clickedMouseButton);

        if (clickedMouseButton == 0) {

            // Get a module from the sidebar.
            modulesInSidebar.stream().filter(module -> Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)).forEach(module -> {
                moduleBeingDragged = module.copy();
                moduleBeingDragged.setX(mouseX - iconSize / 2);
                moduleBeingDragged.setY(mouseY - iconSize / 2);
            });

            // Select a module
            boolean insideAnything = false;
            for (WorktableModule module : modulesOnPaper) {
                if (Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)) {
                    if (moduleSelected != module) {
                        moduleSelected = module;
                        insideAnything = true;
                        break;
                    }
                }
            }
            if (!insideAnything && moduleSelected != null) moduleSelected = null;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (moduleBeingLinked == null && clickedMouseButton == 1) {
            // Link module on paper
            for (WorktableModule module : modulesOnPaper) {
                if (Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)) {
                    moduleBeingLinked = module;
                    break;
                }
            }
        }
        if (clickedMouseButton == 0) {
            // Drag/Readjust module on paper.
            WorktableModule remove = null;
            if (moduleBeingDragged == null)
                for (WorktableModule module : modulesOnPaper) {
                    if (Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)) {
                        if (masterModule == module) masterModule = null;
                        moduleBeingDragged = module;
                        moduleBeingDragged.setX(mouseX);
                        moduleBeingDragged.setY(mouseY);
                        remove = module;
                        break;
                    }
                }

            // Delete module that was on paper but is now a module being dragged
            if (remove != null) {
                if (modulesOnPaper.contains(remove))
                    modulesOnPaper.remove(remove);
                moduleSelected = null;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int clickedMouseButton) {
        if (clickedMouseButton == 0 && moduleBeingDragged != null) {
            if (Utils.isInside(mouseX, mouseY, left + paperLeft, top + paperTop, paperWidth, paperHeight)) {
                // TODO: Config for isMaster on the table
                // Set the module being dragged on the paper
                if (moduleBeingDragged.getModule().getType() == ModuleType.SHAPE) moduleBeingDragged.setMaster(true);
                masterModule = moduleBeingDragged;
                modulesOnPaper.add(moduleBeingDragged);
                moduleBeingDragged = null;
                moduleSelected = null;
            } else {
                // Delete module being dragged if it's outside the paper
                for (WorktableModule module : modulesOnPaper) {
                    if (links.get(moduleBeingDragged).contains(module))
                        links.get(moduleBeingDragged).remove(module);

                    if (links.get(module).contains(moduleBeingDragged))
                        links.get(module).remove(moduleBeingDragged);
                }
                if (modulesOnPaper.contains(moduleBeingDragged))
                    modulesOnPaper.remove(moduleBeingDragged);
                moduleBeingDragged = null;
                moduleSelected = null;
            }
        }

        if (clickedMouseButton == 1) {
            if (moduleBeingLinked != null) {
                boolean insideAnything = false;
                for (WorktableModule module : modulesOnPaper) {
                    if (Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)) {
                        WorktableModule from = moduleBeingLinked;

                        boolean wasLinked = false;

                        if (module.getModule().accept(from.getModule()) || from.getModule().accept(module.getModule())) {
                            if (links.get(from).contains(module)) {
                                links.get(from).remove(module);
                                wasLinked = true;
                            }
                            if (links.get(module).contains(from)) {
                                links.get(module).remove(from);
                                wasLinked = true;
                            }

                            if (!wasLinked) {
                                links.get(module).add(from);
                                links.get(from).add(module);
                            }

                            moduleBeingLinked = null;
                            insideAnything = true;
                        }
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
        WorktableModule moduleBeingHovered = null;

        // RENDER BACKGROUND //
        drawDefaultBackground();

        GlStateManager.color(1F, 1F, 1F, 1F);
        Texture background = new Texture(BACKGROUND_TEXTURE, backgroundWidth, backgroundHeight);
        background.bind();
        background.getSprite(0, 0, backgroundWidth, backgroundHeight).draw(left, top);
        // RENDER BACKGROUND //

        // RENDER BUTTONS //
        // TODO
        GlStateManager.color(1F, 1F, 1F, 1F);
        for (GuiButton button : buttonList)
            if (button.id == Constants.WorkTable.CONFIRM_BUTTON) {
                mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/book/error/error.png"));
                drawScaledCustomSizeModalRect(button.xPosition, button.yPosition, 0, 0, 0, 0, 100, 50, 100, 50);
            } else if (button.id == Constants.WorkTable.DONE_BUTTON) {
                mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/book/error/fof.png"));
                drawScaledCustomSizeModalRect(button.xPosition, button.yPosition, 0, 0, 0, 0, 100, 50, 100, 50);
            }
        // RENDER BUTTONS //

        // SHIMMER CURSOR IF LINKING MODE //
        // TODO
        GlStateManager.color(1F, 1F, 1F, 1F);
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

        // RENDER SIDEBARS //
        GlStateManager.color(1F, 1F, 1F, 1F);
        spriteSheet.bind();
        for (ModuleType type : moduleCategories.keySet()) {
            for (WorktableModule module : moduleCategories.get(type)) {

                // Highlight if hovering over
                if (Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)) {
                    moduleBeingHovered = module;

                } else {
                    Sprite base = spriteSheet.getSprite(33, 208, 23, 23);
                    base.getTex().bind();
                    base.draw(module.getX(), module.getY(), iconSize, iconSize);
                }
            }
        }
        // RENDER SIDEBARS //

        // RENDER LINE BETWEEN LINKED MODULES //
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (moduleBeingDragged != null)
            if (links.containsKey(moduleBeingDragged))
                for (WorktableModule linkedModule : links.get(moduleBeingDragged))
                    Utils.drawLine2D(moduleBeingDragged.getX() + iconSize / 2, moduleBeingDragged.getY() + iconSize / 2, linkedModule.getX() + iconSize / 2, linkedModule.getY() + iconSize / 2, 2, Color.BLACK);

        if (moduleBeingLinked != null)
            Utils.drawLine2D(moduleBeingLinked.getX() + iconSize / 2, moduleBeingLinked.getY() + iconSize / 2, mouseX, mouseY, 2, Color.BLACK);

        modulesOnPaper.stream().filter(module -> links.containsKey(module)).forEach(module -> {
            for (WorktableModule linkedModule : links.get(module))
                Utils.drawLine2D(module.getX() + iconSize / 2, module.getY() + iconSize / 2, linkedModule.getX() + iconSize / 2, linkedModule.getY() + iconSize / 2, 2, Color.BLACK);
        });
        // RENDER LINE BETWEEN LINKED MODULES //

        // RENDER MODULE ON THE PAPER //
        GlStateManager.color(1F, 1F, 1F, 1F);
        for (WorktableModule module : modulesOnPaper) {
            if (moduleSelected != module) {
                if (Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)) {
                    moduleBeingHovered = module;
                } else {
                    Sprite moduleSprite = spriteSheet.getSprite(33, 208, 23, 23);
                    moduleSprite.getTex().bind();
                    moduleSprite.draw(module.getX(), module.getY(), iconSize, iconSize);
                }
            }
        }
        // RENDER MODULE ON THE PAPER //

        // RENDER MODULE BEING DRAGGED //
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (moduleBeingDragged != null) {
            moduleBeingDragged.setX(mouseX - iconSize / 2);
            moduleBeingDragged.setY(mouseY - iconSize / 2);
            Sprite draggingSprite = spriteSheet.getSprite(0, 208, 24, 23);
            draggingSprite.getTex().bind();
            draggingSprite.draw(mouseX - iconSize / 2 - 2, mouseY - iconSize / 2 - 2, iconSize + 4, iconSize + 4);
        }
        // RENDER MODULE BEING DRAGGED //

        // RENDER TOOLTIP & HIGHLIGHT //

        // Highlight module selected
        if (moduleSelected != null) {
            // Render highlight
            GlStateManager.disableLighting();
            Sprite highlight = spriteSheet.getSprite(0, 208, 24, 23);
            highlight.getTex().bind();
            highlight.draw(moduleSelected.getX() - 2, moduleSelected.getY() - 2, iconSize + 4, iconSize + 4);
            GlStateManager.enableLighting();
        }

        // Highlight module being hovered
        if (moduleBeingHovered != null && moduleBeingDragged == null) {
            // Render highlight
            GlStateManager.disableLighting();
            Sprite highlight = spriteSheet.getSprite(0, 208, 24, 23);
            highlight.getTex().bind();
            highlight.draw(moduleBeingHovered.getX(), moduleBeingHovered.getY(), iconSize, iconSize);
            GlStateManager.enableLighting();

            // Render tooltip
            if (modulesOnPaper.contains(moduleBeingHovered) && !isShiftKeyDown()) return;
            List<String> txt = new ArrayList<>();
            txt.add(TextFormatting.GOLD + moduleBeingHovered.getModule().getDisplayName());
            txt.addAll(Utils.padString(moduleBeingHovered.getModule().getDescription(), 30));
            drawHoveringText(txt, mouseX, mouseY, fontRendererObj);
        }
        // RENDER TOOLTIP & HIGHLIGHT //
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
