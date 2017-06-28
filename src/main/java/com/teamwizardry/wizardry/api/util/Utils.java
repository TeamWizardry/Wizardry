package com.teamwizardry.wizardry.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class Utils {

	public static boolean hasOreDictPrefix(ItemStack stack, String dict) {
		int[] ids = OreDictionary.getOreIDs(stack);
		for (int id : ids) {
			if (OreDictionary.getOreName(id).length() >= dict.length()) {
				if (OreDictionary.getOreName(id).substring(0, dict.length()).compareTo(dict.substring(0, dict.length())) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<Object> getVisibleModules(List<Object> objects, double scroll) {
		final int ROWS = 3;
		final int COLS = 5;
		final int SPACES = ROWS * COLS;
		final double MIN_VISIBLE = 0.25; // Minimum portion of a module to be inside the box to be visible

		if (objects.size() <= ROWS * COLS) return objects;

		int rows = MathHelper.ceil((objects.size() - SPACES) / ((double) COLS));
		double rowsScrolled = scroll * rows;
		double scrollMargin = rowsScrolled - (int) rowsScrolled;

		int startIndex = ((int) rowsScrolled) * COLS;
		if (scrollMargin > 1 - MIN_VISIBLE)
			startIndex += COLS;

		int endIndex = startIndex + SPACES;
		if (scrollMargin > MIN_VISIBLE)
			endIndex += COLS;

		if (startIndex < 0)
			startIndex = 0;

		if (endIndex > objects.size())
			endIndex = objects.size();

		List<Object> visible = new ArrayList<>();
		visible.addAll(objects.subList(startIndex, endIndex));

		return visible;
	}
}
