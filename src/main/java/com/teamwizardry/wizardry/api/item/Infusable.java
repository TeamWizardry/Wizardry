package com.teamwizardry.wizardry.api.item;

import net.minecraft.item.ItemStack;

/**
 * Created by Saad on 6/30/2016.
 */
public interface Infusable {

    default PearlType getType(ItemStack stack) {
        if (stack.hasTagCompound())
            if (stack.getTagCompound().hasKey("type"))
                return PearlType.valueOf(stack.getTagCompound().getString("type").toUpperCase());
            else return PearlType.MUNDANE;
        else return PearlType.MUNDANE;
    }
}
