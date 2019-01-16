package com.teamwizardry.wizardry.api.item.pearlswapping;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IPearlSwappable {

	/**
	 * @return The old pearl.
	 */
	@Nonnull
	ItemStack swapPearl(ItemStack pearlHolder, ItemStack stackSwipeTo);

	default void swapOnRightClick(EntityPlayer player, ItemStack pearlHolder) {
		int scrollSlot = ItemNBTHelper.getInt(pearlHolder, "scroll_slot", -1);
		if (scrollSlot == -1) return;

		ItemStack beltStack = BaublesSupport.getItem(player, ModItems.PEARL_BELT);
		if (beltStack.isEmpty()) return;

		IPearlBelt belt = (IPearlBelt) beltStack.getItem();
		ItemStack pearl = belt.removePearl(beltStack, scrollSlot);
		if (pearl.isEmpty()) return;

		ItemStack swappedPearl = swapPearl(pearlHolder, pearl);
		if (!swappedPearl.isEmpty()) {
			belt.addPearl(beltStack, swappedPearl);
		}

		ItemNBTHelper.setInt(pearlHolder, "scroll_slot", Math.max(scrollSlot - 1, 0));
	}
}
