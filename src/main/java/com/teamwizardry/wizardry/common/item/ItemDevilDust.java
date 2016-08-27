package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.common.achievement.Achievements;
import com.teamwizardry.wizardry.common.achievement.IPickupAchievement;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

/**
 * Created by Saad on 6/21/2016.
 */
public class ItemDevilDust extends ItemWizardry implements IPickupAchievement {

    public ItemDevilDust() {
        super("devil_dust");
    }

    @Override
    public Achievement getAchievementOnPickup(ItemStack stack, EntityPlayer player, EntityItem item) {
        return Achievements.DEVILDUST;
    }
}
