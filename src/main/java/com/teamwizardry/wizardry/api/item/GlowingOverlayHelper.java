package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;

public final class GlowingOverlayHelper {

    private static String TAG_OVERLAY = "overlay";

    // All IGlowOverlayable items should use this as a property override.
    public static IItemPropertyGetter OVERLAY_OVERRIDE = (stack, worldIn, entityIn) -> hasOverlay(stack) ? 1f : 0f;

    public static ItemStack overlayStack(ItemStack stack) {
        ItemStack ret = stack.copy();
        ItemNBTHelper.setBoolean(ret, TAG_OVERLAY, true);
        return ret;
    }

    public static boolean hasOverlay(ItemStack stack) {
        return ItemNBTHelper.getBoolean(stack, TAG_OVERLAY, false);
    }
}
