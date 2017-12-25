package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import net.minecraft.item.ItemStack;

import java.util.UUID;

/**
 * Created by LordSaad.
 */
public interface ICape {

	default void tickCape(ItemStack stack) {
		int tick = ItemNBTHelper.getInt(stack, "maxTick", 0);

		if (tick < 1000000) ItemNBTHelper.setInt(stack, "maxTick", ++tick);

		if (!ItemNBTHelper.verifyExistence(stack, "uuid"))
			ItemNBTHelper.setUUID(stack, "uuid", UUID.randomUUID());
	}
}
