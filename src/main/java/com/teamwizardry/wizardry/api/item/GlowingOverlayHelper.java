package com.teamwizardry.wizardry.api.item;

import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class GlowingOverlayHelper {

    private static String TAG_OVERLAY = "overlay";

    // All IGlowOverlayable items should use this as a property override.
    public static IItemPropertyGetter OVERLAY_OVERRIDE = (stack, worldIn, entityIn) -> hasOverlay(stack) ? 1f : 0f;

    public static ItemStack overlayStack(ItemStack stack) {
        ItemStack ret = stack.copy();
        NBTTagCompound comp = ret.getTagCompound();
        if (comp == null) {
            comp = new NBTTagCompound();
            ret.setTagCompound(comp);
        }
        comp.setBoolean(TAG_OVERLAY, true);
        return ret;
    }

    public static boolean hasOverlay(ItemStack stack) {
        NBTTagCompound comp = stack.getTagCompound();
        return comp != null && comp.getBoolean(TAG_OVERLAY);
    }
}
