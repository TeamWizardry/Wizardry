package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.client.Sprite;
import com.teamwizardry.librarianlib.client.Texture;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

/**
 * Created by Saad on 7/7/2016.
 */
public class WorktableSlider {

    private Texture spriteSheet = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/sprite_sheet.png"), 256, 256);
    private Sprite barTop = spriteSheet.getSprite(64, 0, 12, 12);
    private Sprite barMid = spriteSheet.getSprite(64, 16, 12, 12);
    private Sprite barBottom = spriteSheet.getSprite(64, 32, 12, 12);
    private Sprite slider = spriteSheet.getSprite(0, 192, 8, 16);

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
        spriteSheet.bind();

        barTop.draw(left, top);

        for (int i = 0; i < rows - 2; i++) barMid.draw(left, top + separation + i * separation);

        barBottom.draw(left, top + separation * (rows - 1));

        slider.draw(left + (barMid.getWidth() / 2) - (slider.getWidth() / 2), top + slide * separation);
    }
}
