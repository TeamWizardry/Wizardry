package com.teamwizardry.wizardry.client.gui.worktable;

import com.teamwizardry.librarianlib.api.gui.GuiTickHandler;

import java.util.ArrayList;

import static com.teamwizardry.wizardry.lib.LibSprites.Worktable.*;

/**
 * Created by Saad on 7/7/2016.
 */
public class WorktableSlider {

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
		SPRITE_SHEET.bind();
		SCROLL_GROOVE_VERTICAL_TOP.draw(GuiTickHandler.ticks, left, top);

		for (int i = 0; i < rows - 2; i++)
			SCROLL_GROOVE_VERTICAL_MIDDLE.draw(GuiTickHandler.ticks, left, top + separation + i * separation);

		SCROLL_GROOVE_VERTICAL_BOTTOM.draw(GuiTickHandler.ticks, left, top + separation * (rows - 1));

		SCROLL_SLIDER_VERTICAL.draw(GuiTickHandler.ticks, left + (SCROLL_GROOVE_VERTICAL_MIDDLE.width / 2) - (SCROLL_SLIDER_VERTICAL.height / 2), top + slide * separation);
	}
}