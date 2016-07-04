package com.teamwizardry.wizardry.client.gui.worktable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.librarianlib.api.util.misc.Utils;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableGui extends GuiScreen {

    private static int left, top, right;
    private static int backgroundWidth = 214, backgroundHeight = 220; // SIZE OF PAPER
    private static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sample-page-background.png");
    private HashMap<ModuleType, ArrayList<WorktableModule>> moduleCategories;
    private ArrayList<WorktableModule> modulesInSidebar;
    private ArrayList<WorktableModule> modulesOnPaper;
    private Multimap<WorktableModule, WorktableModule> links;
    private WorktableModule moduleBeingDragged, moduleBeingLinked;
    private int iconSize = 16;
    private int rotateShimmer = 0;

    @Override
    public void initGui() {
        left = width / 2 - backgroundWidth / 2;
        top = height / 2 - backgroundHeight / 2;
        right = (width / 2 + backgroundWidth / 2) - 6;

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
        for (ModuleList.IModuleConstructor moduleConstructor : Wizardry.moduleList.modules.values()) {
            // Construct a new module object
            Module module = moduleConstructor.construct();
            module.setIcon(new ResourceLocation(Wizardry.MODID, "textures/items/manaIconOutline.png"));

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
            int row = 0, column = 0, sidebarLeft = 0, sidebarTop = 0;
            switch (type) {
                case BOOLEAN:
                    sidebarLeft = left - 150;
                    sidebarTop = top + 100;
                    break;
                case SHAPE:
                    sidebarLeft = left - 150;
                    sidebarTop = top;
                    break;
                case EVENT:
                    sidebarLeft = left - 70;
                    sidebarTop = top;
                    break;
                case EFFECT:
                    sidebarLeft = right + 10;
                    sidebarTop = top;
                    break;
                case MODIFIER:
                    sidebarLeft = right + 100;
                    sidebarTop = top;
            }

            // Add the actual module into the calculated sidebar positions
            for (WorktableModule module : moduleCategories.get(type)) {

                int iconSeparation = 1;
                int x = sidebarLeft + (row * iconSize) + (column * iconSeparation);
                int y = sidebarTop + (column * iconSize) + (column * iconSeparation);

                module.setX(x);
                module.setY(y);

                if (row >= 3) {
                    row = 0;
                    column++;
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

        // Get a module from the sidebar.
        for (WorktableModule module : modulesInSidebar) {
            boolean inside = mouseX >= module.getX() && mouseX < module.getX() + iconSize && mouseY >= module.getY() && mouseY < module.getY() + iconSize;
            if (inside) {
                moduleBeingDragged = module.copy();
                moduleBeingDragged.setX(mouseX - iconSize / 2);
                moduleBeingDragged.setY(mouseY - iconSize / 2);
            }
        }

        // Drag/Readjust module on paper.
        for (WorktableModule module : modulesOnPaper) {
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
            for (WorktableModule module : modulesOnPaper) {
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
                for (WorktableModule module : modulesOnPaper) {
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
                for (WorktableModule module : modulesOnPaper) {
                    boolean inside = mouseX >= module.getX() - iconSize / 2 && mouseX < module.getX() - iconSize / 2 + iconSize && mouseY >= module.getY() - iconSize / 2 && mouseY < module.getY() - iconSize / 2 + iconSize;
                    if (inside) {
                        WorktableModule from = moduleBeingLinked;

                        boolean wasLinked = false;

                        if (links.get(from).contains(module)) {
                            links.get(from).remove(module);
                            wasLinked = true;
                        }
                        if (links.get(module).contains(from)) {
                            links.get(module).remove(from);
                            wasLinked = true;
                        }

                        if (!wasLinked) links.get(from).add(module);

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

        // RENDER LINE BETWEEN LINKED MODULES //
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (moduleBeingDragged != null)
            if (links.containsKey(moduleBeingDragged))
                for (WorktableModule linkedModule : links.get(moduleBeingDragged))
                    Utils.drawLine2D(moduleBeingDragged.getX(), moduleBeingDragged.getY(), linkedModule.getX(), linkedModule.getY(), 2, Color.BLACK);

        if (moduleBeingLinked != null)
            Utils.drawLine2D(moduleBeingLinked.getX(), moduleBeingLinked.getY(), mouseX, mouseY, 2, Color.BLACK);

        modulesOnPaper.stream().filter(module -> links.containsKey(module)).forEach(module -> {
            for (WorktableModule linkedModule : links.get(module))
                Utils.drawLine2D(module.getX(), module.getY(), linkedModule.getX(), linkedModule.getY(), 2, Color.BLACK);
        });
        // RENDER LINE BETWEEN LINKED MODULES //

        // RENDER SIDEBARS //
        GlStateManager.color(1F, 1F, 1F, 1F);
        for (ModuleType type : moduleCategories.keySet()) {
            for (WorktableModule module : moduleCategories.get(type)) {

                // Highlight
                boolean inside = mouseX >= module.getX() && mouseX < module.getX() + iconSize && mouseY >= module.getY() && mouseY < module.getY() + iconSize;
                if (inside) {
                    mc.renderEngine.bindTexture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/blue-gradient.png"));
                    GlStateManager.color(1F, 1F, 1F, 1F);
                    drawScaledCustomSizeModalRect(module.getX() - iconSize / 2, module.getY() - iconSize / 2, 0, 0, iconSize * 2, iconSize * 2, iconSize * 2, iconSize * 2, iconSize * 2, iconSize * 2);
                }

                // Render the actual icon
                mc.renderEngine.bindTexture(module.getModule().getIcon());
                drawScaledCustomSizeModalRect(module.getX(), module.getY(), 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
            }
        }
        // RENDER SIDEBARS //

        // RENDER MODULE ON CURSOR //
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (moduleBeingDragged != null) {
            moduleBeingDragged.setX(mouseX);
            moduleBeingDragged.setY(mouseY);
            mc.renderEngine.bindTexture(moduleBeingDragged.getModule().getIcon());
            drawScaledCustomSizeModalRect(moduleBeingDragged.getX() - iconSize / 2, moduleBeingDragged.getY() - iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
        }
        // RENDER MODULE ON CURSOR //

        // RENDER MODULE ON THE PAPER //
        GlStateManager.color(1F, 1F, 1F, 1F);
        for (WorktableModule module : modulesOnPaper) {
            mc.renderEngine.bindTexture(module.getModule().getIcon());
            drawScaledCustomSizeModalRect(module.getX() - iconSize / 2, module.getY() - iconSize / 2, 0, 0, iconSize, iconSize, iconSize, iconSize, iconSize, iconSize);
        }
        // RENDER MODULE ON THE PAPER //
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
