package com.teamwizardry.wizardry.common.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by Demoniaque on 7/1/2016.
 */
public interface IPickupAchievement {

	Advancement getAdvancementOnPickup(ItemStack stack, EntityPlayer player, EntityItem item);
}
