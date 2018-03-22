package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
public interface ICape {

	default void tickCape(ItemStack stack) {
		int tick = ItemNBTHelper.getInt(stack, "maxTick", 0);

		if (tick < 1000000) ItemNBTHelper.setInt(stack, "maxTick", ++tick);

		if (!ItemNBTHelper.verifyExistence(stack, "uuid"))
			ItemNBTHelper.setUUID(stack, "uuid", UUID.randomUUID());
	}

	default List<String> getCapeTooltip(ItemStack stack) {
		List<String> list = new ArrayList<>();

		double tick = ItemNBTHelper.getInt(stack, "maxTick", 0) / 1000000.0;

		list.add(TextFormatting.GRAY.toString() + "Spell Cost Reduction: ");
		list.add(TextFormatting.YELLOW.toString() + Math.round(tick * 100) + "%");

		return list;
	}
}
