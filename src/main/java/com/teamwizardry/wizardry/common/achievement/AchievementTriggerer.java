package com.teamwizardry.wizardry.common.achievement;

import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Created by Saad on 7/1/2016.
 */
public class AchievementTriggerer {

    @SubscribeEvent
    public void pickup(PlayerEvent.ItemPickupEvent event) {
        ItemStack stack = event.pickedUp.getEntityItem();
        if (stack.getItem() instanceof IPickupAchievement) {
            Achievement achievement = ((IPickupAchievement) stack.getItem()).getAchievementOnPickup(stack, event.player, event.pickedUp);
            if (achievement != null)
                event.player.addStat(achievement, 1);
        }

    }
}
