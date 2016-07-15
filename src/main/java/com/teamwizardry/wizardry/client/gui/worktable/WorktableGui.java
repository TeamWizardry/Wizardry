package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.api.gui.GuiBase;
import com.teamwizardry.librarianlib.api.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.api.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.api.util.misc.Utils;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.client.Texture;
import com.teamwizardry.librarianlib.math.Vec2;
import com.teamwizardry.librarianlib.math.shapes.BezierCurve2D;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by Saad on 6/17/2016.
 */
public class WorktableGui extends GuiBase {

    static final int iconSize = 12;
    private int left, top, paperLeft = 160, paperTop = 0;
    private int backgroundWidth = 512, backgroundHeight = 256, paperWidth = 191, paperHeight = 202;
    private int rotateShimmer = 0;

    private HashMap<ModuleType, ArrayList<WorktableModule>> moduleCategories;
    private HashMap<ModuleType, WorktableSlider> categorySlidebars;

    private ArrayList<WorktableModule> modulesInSidebar;
    private ArrayList<WorktableModule> modulesOnPaper;
    private ArrayList<WorktableLink> moduleLinks;

    private BezierCurve2D curveModuleBeingLinked;
    private WorktableModule moduleBeingDragged, moduleBeingLinked, masterModule, moduleSelected;

    private Texture spriteSheet = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sprite_sheet.png"), 256, 256);
    private Texture BACKGROUND_TEXTURE = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/table_background.png"), backgroundWidth, backgroundHeight);

    public WorktableGui(int guiWidth, int guiHeight) {
        super(guiWidth, guiHeight);

        ComponentVoid v = new ComponentVoid(0, 0);
        v.preDraw.add((comp, pos, ticks) -> GlStateManager.translate(0, 0, 5));
        ComponentVoid v2 = new ComponentVoid(0, 0);
        v2.preDraw.add((comp, pos, ticks) -> GlStateManager.translate(0, 0, -5));

        ComponentSprite comp = new ComponentSprite(spriteSheet.getSprite(33, 208, 23, 23), 100, 100, 12, 12);

        AtomicBoolean tracking = new AtomicBoolean(false);
        AtomicReference<Vec2> clickStart = new AtomicReference<>(new Vec2(0, 0));

        comp.mouseDown.add((c, pos, button) -> {
            if (c.mouseOverThisFrame) {
                c.setSize(new Vec2(24, 24));
                tracking.set(true);
                clickStart.set(pos.add(6, 6));
                return true;
            }
            return false;
        });
        comp.mouseUp.add((c, pos, button) -> {
            if (tracking.get()) {
                c.setPos(c.getPos().add(new Vec2(6, 6)));
                c.setSize(new Vec2(12, 12));
            }
            tracking.set(false);
            return false;
        });
        comp.preDraw.add((c, pos, ticks) -> {
            if (tracking.get()) {
                c.setPos(
                        c.getPos().add(pos)
                                .sub(clickStart.get())
                );
            }
        });

        v.zIndex = 50;
        comp.zIndex = 100;
        v2.zIndex = 150;

        components.add(v);
        components.add(v2);
        components.add(comp);
    }

    @Override
    public void initGui() {
        super.initGui();
        left = width / 2 - backgroundWidth / 2;
        top = height / 2 - backgroundHeight / 2;

        moduleCategories = new HashMap<>();
        categorySlidebars = new HashMap<>();
        modulesInSidebar = new ArrayList<>();
        modulesOnPaper = new ArrayList<>();
        moduleLinks = new ArrayList<>();

        initModules();
    }

    private void initModules() {
        // Construct the new module
        for (ModuleList.IModuleConstructor moduleConstructor : ModuleList.INSTANCE.modules.values()) {
            // Construct a new module object
            Module module = moduleConstructor.construct();

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
            int row = 0, maxColumns = 2, maxRows = 2, column = 0, sidebarLeft = 0, sidebarTop = 0;
            switch (type) {
                case BOOLEAN:
                    sidebarLeft = left + 39;
                    sidebarTop = top + 123;
                    maxRows = 5;
                    if (moduleCategories.get(type).size() >= 15) {
                        maxColumns = 2;
                        categorySlidebars.put(type, new WorktableSlider(maxRows, maxColumns, sidebarLeft + iconSize * 2 + 1, sidebarTop, moduleCategories.get(type)));
                    } else maxColumns = 3;
                    break;
                case SHAPE:
                    sidebarLeft = left + 39;
                    sidebarTop = top + 39;
                    maxRows = 5;
                    if (moduleCategories.get(type).size() >= 15) {
                        maxColumns = 2;
                        categorySlidebars.put(type, new WorktableSlider(maxRows, maxColumns, sidebarLeft + iconSize * 2 + 1, sidebarTop, moduleCategories.get(type)));
                    } else maxColumns = 3;
                    break;
                case EVENT:
                    sidebarLeft = left + 375;
                    sidebarTop = top + 38;
                    maxRows = 6;
                    if (moduleCategories.get(type).size() >= 18) {
                        maxColumns = 2;
                        categorySlidebars.put(type, new WorktableSlider(maxRows, maxColumns, sidebarLeft + iconSize * 2 + 1, sidebarTop, moduleCategories.get(type)));
                    } else maxColumns = 3;
                    break;
                case EFFECT:
                    sidebarLeft = left + 99;
                    sidebarTop = top + 39;
                    maxRows = 12;
                    // TODO: TESTING HERE //
                    maxColumns = 2;
                    categorySlidebars.put(type, new WorktableSlider(maxRows, maxColumns, sidebarLeft + iconSize * 2 + 1, sidebarTop, moduleCategories.get(type)));
                    /*if (moduleCategories.get(type).size() >= 36) {
                        maxColumns = 2;
                        categorySlidebars.put(type, new WorktableSlider(maxRows, maxColumns, sidebarLeft + iconSize * 2 + 1, sidebarTop, moduleCategories.get(type)));
                    } else maxColumns = 3;*/
                    // TODO: TESTING HERE //
                    break;
                case MODIFIER:
                    sidebarLeft = left + 435;
                    sidebarTop = top + 38;
                    maxRows = 6;
                    if (moduleCategories.get(type).size() >= 18) {
                        maxColumns = 2;
                        categorySlidebars.put(type, new WorktableSlider(maxRows, maxColumns, sidebarLeft + iconSize * 2 + 1, sidebarTop, moduleCategories.get(type)));
                    } else maxColumns = 3;
                    break;
            }

            // Add the actual module into the calculated sidebar positions
            for (WorktableModule module : moduleCategories.get(type)) {

                int iconSeparation = 0;
                int x = sidebarLeft + (row * iconSize) + (row * iconSeparation);
                int y = sidebarTop + (column * iconSize) + (column * iconSeparation);

                module.setX(x);
                module.setY(y);

                if (row >= maxColumns - 1) {
                    row = 0;
                    if (column < maxRows) column++;
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
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (moduleBeingLinked == null && clickedMouseButton == 1) {
            // Link module on paper
            for (WorktableModule module : modulesOnPaper) {
                if (Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)) {
                    moduleBeingLinked = module;
                    curveModuleBeingLinked = new BezierCurve2D(new Vec2(module.getX() + iconSize / 2, module.getY() + iconSize / 2), new Vec2(mouseX, mouseY));
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
        super.mouseReleased(mouseX, mouseY, clickedMouseButton);
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

                List<WorktableLink> concurrentLinks = moduleLinks.stream().filter(link -> link.getEndPointModule() == moduleBeingDragged || link.getStartPointModule() == moduleBeingDragged).collect(Collectors.toList());
                moduleLinks.removeAll(concurrentLinks);

                if (modulesOnPaper.contains(moduleBeingDragged)) modulesOnPaper.remove(moduleBeingDragged);
                moduleBeingDragged = null;
                moduleSelected = null;
            }
        }

        if (clickedMouseButton == 1) {
            if (moduleBeingLinked != null) {
                boolean insideAnything = false;
                for (WorktableModule to : modulesOnPaper) {
                    if (Utils.isInside(mouseX, mouseY, to.getX(), to.getY(), iconSize)) {
                        WorktableModule from = moduleBeingLinked;

                        boolean wasLinked = false;

                        // Remove a link if it's already established on either side
                        for (WorktableLink link : moduleLinks)
                            if (link.getStartPointModule() == from && link.getEndPointModule() == to) {
                                moduleLinks.remove(link);
                                wasLinked = true;
                                break;
                            } else if (link.getStartPointModule() == to && link.getEndPointModule() == from) {
                                moduleLinks.remove(link);
                                wasLinked = true;
                                break;
                            }

                        if (to.getModule().accept(from.getModule())) {

                            // There was no link, make one
                            if (!wasLinked) moduleLinks.add(new WorktableLink(from, to));

                            curveModuleBeingLinked = null;
                            moduleBeingLinked = null;
                            insideAnything = true;
                        }
                        break;
                    }
                }

                // The mouse linking was never in a module to begin with, remove the mouse link
                if (!insideAnything) {
                    moduleBeingLinked = null;
                    curveModuleBeingLinked = null;
                }
            } else {
                moduleBeingLinked = null;
                curveModuleBeingLinked = null;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        WorktableModule moduleBeingHovered = null;

        // RENDER BACKGROUND //
        drawDefaultBackground();

        GlStateManager.color(1F, 1F, 1F, 1F);
        BACKGROUND_TEXTURE.bind();
        BACKGROUND_TEXTURE.getSprite(0, 0, backgroundWidth, backgroundHeight).draw(left, top);
        // RENDER BACKGROUND //

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

        // RENDER MODULES IN THE SIDEBARS //
        GlStateManager.color(1F, 1F, 1F, 1F);
        spriteSheet.bind();
        for (ModuleType type : moduleCategories.keySet()) {
            if (categorySlidebars.containsKey(type)) {
                for (WorktableModule module : categorySlidebars.get(type).getModules()) {
                    // Highlight if hovering over
                    if (Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)) {
                        moduleBeingHovered = module;
                    } else {
                        Sprite base = spriteSheet.getSprite(33, 208, 23, 23);
                        base.getTex().bind();
                        base.draw(module.getX(), module.getY(), iconSize, iconSize);
                    }
                }
            } else {
                for (WorktableModule module : moduleCategories.get(type)) {
                    // Highlight if hovering over
                    if (Utils.isInside(mouseX, mouseY, module.getX(), module.getY(), iconSize)) {
                        moduleBeingHovered = module;
                    } else {
                        Sprite base = spriteSheet.getSprite(33, 208, 23, 23);
                        base.getTex().bind();
                        base.draw(module.getX(), module.getY(), iconSize, iconSize);

                        Sprite icon = new Sprite(module.getModule().getIcon());
                        icon.getTex().bind();
                        icon.draw(module.getX(), module.getY(), iconSize, iconSize);
                    }
                }
            }
        }
        GlStateManager.color(1F, 1F, 1F, 1F);
        for (ModuleType type : categorySlidebars.keySet()) categorySlidebars.get(type).draw();
        // RENDER MODULES IN THE SIDEBARS //

        // RENDER LINE BETWEEN LINKED MODULES //
        GlStateManager.color(1F, 1F, 1F, 1F);
        if (moduleBeingDragged != null) {
            for (WorktableLink link : moduleLinks) {
                if (link.getStartPointModule() == moduleBeingDragged) link.setStartPointModule(moduleBeingDragged);
                else if (link.getEndPointModule() == moduleBeingDragged) link.setEndPointModule(moduleBeingDragged);
                link.draw();
            }
        }

        if (moduleBeingLinked != null && curveModuleBeingLinked != null) {
            curveModuleBeingLinked.setStartPoint(new Vec2(mouseX, mouseY));
            curveModuleBeingLinked.draw();
        }

        moduleLinks.stream().filter(link -> link.getStartPointModule() != moduleBeingDragged && link.getEndPointModule() != moduleBeingDragged).forEach(BezierCurve2D::draw);
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
            Sprite draggingSprite = spriteSheet.getSprite(0, 208, 24, 24);
            draggingSprite.getTex().bind();
            draggingSprite.draw(mouseX - iconSize / 2 - 2, mouseY - iconSize / 2 - 2, iconSize + 4, iconSize + 4);
        }
        // RENDER MODULE BEING DRAGGED //

        // RENDER TOOLTIP & HIGHLIGHT //
        // Highlight module selected
        if (moduleSelected != null) {
            // Render highlight
            GlStateManager.disableLighting();
            Sprite highlight = spriteSheet.getSprite(0, 208, 24, 24);
            highlight.getTex().bind();
            highlight.draw(moduleSelected.getX() - 2, moduleSelected.getY() - 2, iconSize + 4, iconSize + 4);
            GlStateManager.enableLighting();
        }

        // Highlight module being hovered
        if (moduleBeingHovered != null && moduleBeingDragged == null) {
            // Render highlight
            GlStateManager.disableLighting();
            Sprite highlight = spriteSheet.getSprite(0, 208, 24, 24);
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
