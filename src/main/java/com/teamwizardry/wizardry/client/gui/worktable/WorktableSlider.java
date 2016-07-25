package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.api.gui.GuiTickHandler;
import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.client.Texture;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

/**
 * Created by Saad on 7/7/2016.
 */
public class WorktableSlider {

    private Sprite barTop = WorktableGui.SCROLL_GROOVE_V_TOP;
    private Sprite barMid = WorktableGui.SCROLL_GROOVE_V;
    private Sprite barBottom = WorktableGui.SCROLL_GROOVE_V_BOTTOM;
    private Sprite slider = WorktableGui.SCROLL_SLIDER_V;

    private ArrayList<WorktableModule> modules;

    private int rows, columns, left, top, slide;
    private int separation = 12;

    public WorktableSlider(int rows, int columns, int left, int top, ArrayList<WorktableModule> modules) {
        this.rows = rows;
        this.columns = columns;
        this.left = left;
        this.top = top;
        slide = 0;
        this.modules = modules;
    }

    public int getTop() {
        return top;
    }

    public int getLeft() {
        return left;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getSeparation() {
        return separation;
    }

    public ArrayList<WorktableModule> getModules() {
        int rowSkip = slide / rows;
        ArrayList<WorktableModule> adjustedModules = new ArrayList<>();
        for (int i = 0; i < rows * columns; i++) {
            if (modules.size() > i + rowSkip * columns)
                adjustedModules.add(modules.get(i + rowSkip * columns));
            else break;
        }
        return adjustedModules;
    }

    public int getSlide() {
        return slide;
    }

    public void setSlide(int slide) {
        this.slide = slide;
    }

    public void draw() {
        barTop.getTex().bind();

        barTop.draw(GuiTickHandler.ticks, left, top);

        for (int i = 0; i < rows - 2; i++) barMid.draw(GuiTickHandler.ticks, left, top + separation + i * separation);

        barBottom.draw(GuiTickHandler.ticks, left, top + separation * (rows - 1));

        slider.draw(GuiTickHandler.ticks, left + (barMid.width / 2) - (slider.height / 2), top + slide * separation);
    }
}