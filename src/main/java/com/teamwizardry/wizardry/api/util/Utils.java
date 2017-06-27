package com.teamwizardry.wizardry.api.util;

import java.util.ArrayList;
import java.util.List;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.Module;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.oredict.OreDictionary;

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
	
	public static List<Module> getVisibleModules(List<Module> modules, double scroll)
	{
		final int ROWS = 5;
		final int COLS = 3;
		final int SPACES = ROWS * COLS;
		final double MIN_VISIBLE = 0.25; // Minimum portion of a module to be inside the box to be visible
		
		if (modules.size() <= ROWS * COLS)
			return modules;
		int rows = MathHelper.ceil((modules.size() - SPACES) / ((double) COLS));
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
		
		if (endIndex > modules.size())
			endIndex = modules.size();
		
		List<Module> visible = new ArrayList<>();
		for (Module m : modules.subList(startIndex, endIndex))
			visible.add(m);
		
		return visible;
	}
}
