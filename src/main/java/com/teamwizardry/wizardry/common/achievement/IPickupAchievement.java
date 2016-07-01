package com.teamwizardry.wizardry.common.achievement;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

/**
 * Created by Saad on 7/1/2016.
 */
public interface IPickupAchievement {

    Achievement getAchievementOnPickup(ItemStack stack, EntityPlayer player, EntityItem item);
}
