package com.teamwizardry.wizardry.common.item.wheels;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandler;

import java.util.Iterator;

/**
 * @author WireSegal
 * Created at 12:23 PM on 3/4/18.
 */
public interface IPearlWheelHolder {
	IItemHandler pearls(ItemStack stack);

	/**
	 * Return false when this belt is disabled for some reason.
	 */
	default boolean shouldUse(ItemStack stack) {
		return true;
	}

	static ItemStack getPearlHolder(EntityPlayer player) {
		EventPearlInventories event = new EventPearlInventories(player);

		event.addItems(player.inventory.mainInventory, 0);
		event.addItems(player.inventory.armorInventory, 1);
		event.addItems(player.inventory.offHandInventory, 10000);

		MinecraftForge.EVENT_BUS.post(event);

		Iterator<ItemStack> stacks = event.getCombinedIterator();
		while (stacks.hasNext()) {
			ItemStack next = stacks.next();
			if (next.getItem() instanceof IPearlWheelHolder && ((IPearlWheelHolder) next.getItem()).shouldUse(next))
				return next;
		}
		return ItemStack.EMPTY;
	}
}
