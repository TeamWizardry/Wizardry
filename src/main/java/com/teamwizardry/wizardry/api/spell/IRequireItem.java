package com.teamwizardry.wizardry.api.spell;

import net.minecraft.item.ItemStack;

public interface IRequireItem {
    void handle(ItemStack stack);
}
