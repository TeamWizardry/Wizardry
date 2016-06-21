package me.lordsaad.wizardry.api.modules;

import net.minecraft.item.ItemStack;

/**
 * Created by Saad on 6/21/2016.
 */
public interface IModule {

    ItemStack getItem();

    ModuleType getType();
}
