package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants.NBT;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;

public class GlowingOverlayHelper {

	// All IGlowOverlayable items should use this as a property override.
	public static IItemPropertyGetter OVERLAY_OVERRIDE = (stack, worldIn, entityIn) -> hasOverlay(stack) ? 1.0f : 0.0f;

	public static ItemStack overlayStack(ItemStack stack) {
		ItemStack ret = stack.copy();
		ItemNBTHelper.setBoolean(ret, NBT.TAG_OVERLAY, true);
		return ret;
	}

	public static boolean hasOverlay(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, NBT.TAG_OVERLAY, false);
	}
}
