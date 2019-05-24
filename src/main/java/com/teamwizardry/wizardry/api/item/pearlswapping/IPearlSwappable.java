package com.teamwizardry.wizardry.api.item.pearlswapping;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.common.item.pearlbelt.IPearlBelt;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Implement this for any item that can interact with pearl belts or similar.
 * Will enable the pearl selection UI.
 */
public interface IPearlSwappable {

	/**
	 * @return The old pearl.
	 */
	@Nonnull
	ItemStack swapPearl(ItemStack pearlHolder, ItemStack stackSwipeTo);

	default void swapOnRightClick(EntityPlayer player, ItemStack pearlHolder) {
		int scrollSlot = NBTHelper.getInt(pearlHolder, "scroll_slot", -1);
		if (scrollSlot == -1) return;

		ItemStack beltStack = BaublesSupport.getItem(player, ModItems.PEARL_BELT);
		if (beltStack.isEmpty()) return;

		IPearlBelt belt = (IPearlBelt) beltStack.getItem();
		ItemStack pearl = belt.removePearl(beltStack, scrollSlot, true);
		if (pearl.isEmpty()) return;

		ItemStack swappedPearl = swapPearl(pearlHolder, pearl);
		if (!swappedPearl.isEmpty()) {
			belt.addPearl(beltStack, swappedPearl, true);
		}

		NBTHelper.setInt(pearlHolder, "scroll_slot", Math.max(scrollSlot - 1, 0));

		//	if (player instanceof EntityPlayerMP)
		//		PacketHandler.NETWORK.sendTo(new PacketSetScrollSlotClient(Utils.getSlotFor(player, pearlHolder), -1), (EntityPlayerMP) player);
	}
}
