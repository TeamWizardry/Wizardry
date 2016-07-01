package com.teamwizardry.wizardry.common.achievement;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

/**
 * Created by Saad on 7/1/2016.
 */
public class ModAchievement extends Achievement {

    public ModAchievement(String unlocalizedName, int column, int row, ItemStack stack, Achievement parent) {
        super("achievement.wizardry." + unlocalizedName, "wizardry." + unlocalizedName, column, row, stack, parent);
        registerStat();
    }

    public ModAchievement(String unlocalizedName, int column, int row, Block blockIn, Achievement parent) {
        this(unlocalizedName, column, row, new ItemStack(blockIn), parent);
    }

    public ModAchievement(String unlocalizedName, int column, int row, Item itemIn, Achievement parent) {
        this(unlocalizedName, column, row, new ItemStack(itemIn), parent);
    }
}
