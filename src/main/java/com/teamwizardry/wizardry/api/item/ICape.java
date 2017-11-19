package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import net.minecraft.item.ItemStack;

/**
 * Created by LordSaad.
 */
public interface ICape {

	default void tickCape(ItemStack stack) {
		int tick = ItemNBTHelper.getInt(stack, "tick", 0);

		if (tick < 1000000) ItemNBTHelper.setInt(stack, "tick", ++tick);
	}
}
