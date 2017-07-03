package com.teamwizardry.wizardry.api.util;

import com.teamwizardry.librarianlib.features.gui.GuiComponent;
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

	public static List<GuiComponent<?>> getVisibleComponents(List<GuiComponent<?>> objects, double scroll) {
		final int ROWS = 3;
		final int COLS = 5;
		final int SPACES = ROWS * COLS;

		if (objects.size() <= ROWS * COLS) return objects;

		int rows = MathHelper.ceil((objects.size() - SPACES) / ((double) COLS));
		double rowsScrolled = scroll * rows;

		int startIndex = ((int) rowsScrolled) * COLS;

		int endIndex = startIndex + SPACES;

		if (startIndex < 0)
			startIndex = 0;

		if (endIndex > objects.size())
			endIndex = objects.size();

		List<GuiComponent<?>> visible = new ArrayList<>();
		visible.addAll(objects.subList(startIndex, endIndex));

		return visible;
	}
}
