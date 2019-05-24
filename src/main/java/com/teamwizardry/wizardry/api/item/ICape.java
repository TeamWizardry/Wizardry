package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
public interface ICape {

	default void tickCape(ItemStack stack) {
		int tick = NBTHelper.getInt(stack, "maxTick", 0);

		if (tick < 1000000) NBTHelper.setInt(stack, "maxTick", ++tick);

		if (!NBTHelper.hasNBTEntry(stack, "uuid"))
			NBTHelper.setUniqueId(stack, "uuid", UUID.randomUUID());
	}

	default List<String> getCapeTooltip(ItemStack stack) {
		List<String> list = new ArrayList<>();

		double tick = NBTHelper.getInt(stack, "maxTick", 0) / 1000000.0;
		double percentage = Math.round(MathHelper.clamp(tick, 0, 0.75) * 100.0);

		list.add(TextFormatting.GRAY.toString() + "Spell Cost Reduction: ");
		list.add(TextFormatting.YELLOW.toString() + percentage + "%");

		return list;
	}
}
