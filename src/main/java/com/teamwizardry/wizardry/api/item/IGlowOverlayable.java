package com.teamwizardry.wizardry.api.item;

import net.minecraft.item.ItemStack;

public interface IGlowOverlayable {
    default boolean useOverlay(ItemStack stack) {
        return true;
    }

    default boolean useShader(ItemStack stack) {
        return true;
    }

    default boolean disableLighting(ItemStack stack) {
        return true;
    }
}
