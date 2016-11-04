package com.teamwizardry.wizardry.common.achievement;

import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;

/**
 * Created by Saad on 7/1/2016.
 */
public class AchievementEvents {

	@SubscribeEvent
	public void pickup(ItemPickupEvent event) {
		ItemStack stack = event.pickedUp.getEntityItem();
		if (stack.getItem() instanceof IPickupAchievement) {
			Achievement achievement = ((IPickupAchievement) stack.getItem()).getAchievementOnPickup(stack, event.player, event.pickedUp);
			if (achievement != null)
				event.player.addStat(achievement, 1);
		}
	}

	@SubscribeEvent
	public void onAchievement(AchievementEvent event) {
		if (ModAchievement.achievements.contains(event.getAchievement()) && !event.getEntityPlayer().hasAchievement(event.getAchievement())) {
			Achievement parent = event.getAchievement().parentAchievement;
			if ((parent == null) || event.getEntityPlayer().hasAchievement(parent))
				event.getEntity().worldObj.playSound(null, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, ModSounds.BELL, SoundCategory.BLOCKS, 0.3F, 1F);
		}
	}
}
