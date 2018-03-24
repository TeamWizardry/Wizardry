package com.teamwizardry.wizardry.common.advancement;

/**
 * Created by Demoniaque on 7/1/2016.
 */
public class AchievementEvents {

	//@SubscribeEvent
	//public void pickup(ItemPickupEvent event) {
	//	ItemStack stack = event.pickedUp.getItem();
	//	if (stack.getItem() instanceof IPickupAchievement) {
	//		Achievement advancement = ((IPickupAchievement) stack.getItem()).getAdvancementOnPickup(stack, event.player, event.pickedUp);
	//		if (advancement != null)
	//			event.player.addStat(advancement, 1);
	//	}
	//}
//
	//@SubscribeEvent
	//public void onAchievement(AchievementEvent event) {
	//	if (ModAdvancement.achievements.contains(event.getAchievement()) && !event.getEntityPlayer().hasAchievement(event.getAchievement())) {
	//		Achievement parent = event.getAchievement().parentAchievement;
	//		if ((parent == null) || event.getEntityPlayer().hasAchievement(parent))
	//			event.getEntity().world.playSound(null, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, ModSounds.BELL, SoundCategory.BLOCKS, 0.3F, 1F);
	//	}
	//}
}
