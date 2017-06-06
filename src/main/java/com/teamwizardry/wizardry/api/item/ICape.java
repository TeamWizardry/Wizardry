package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import java.util.UUID;

/**
 * Created by LordSaad.
 */
public interface ICape {

	default void tickCape(ItemStack stack, EntityLivingBase entityIn) {
		int tick = ItemNBTHelper.getInt(stack, "tick", 0);
		if (tick < 300) ItemNBTHelper.setInt(stack, "tick", tick + 1);
		else {
			ItemNBTHelper.setInt(stack, "tick", 0);

			int time = ItemNBTHelper.getInt(stack, "time", 0);
			UUID owner = ItemNBTHelper.getUUID(stack, "owner");
			UUID thief = ItemNBTHelper.getUUID(stack, "thief");

			if (owner == null) {
				ItemNBTHelper.setUUID(stack, "owner", entityIn.getUniqueID());
				owner = entityIn.getUniqueID();
			}

			UUID entity = entityIn.getUniqueID();
			if (!owner.equals(entity)) {
				int buffer = ItemNBTHelper.getInt(stack, "buffer", 0);
				if (buffer >= 30) {
					ItemNBTHelper.setUUID(stack, "owner", entityIn.getUniqueID());
					ItemNBTHelper.setInt(stack, "time", (int) (time / 1.5));
					ItemNBTHelper.removeEntry(stack, "buffer");
				} else {
					if (thief != entityIn.getUniqueID()) {
						ItemNBTHelper.setInt(stack, "buffer", 0);
						ItemNBTHelper.setUUID(stack, "thief", entityIn.getUniqueID());
					} else ItemNBTHelper.setInt(stack, "buffer", buffer + 1);
				}
			} else {
				ItemNBTHelper.setInt(stack, "time", time + 1);
				ItemNBTHelper.removeEntry(stack, "thief");
				ItemNBTHelper.removeEntry(stack, "buffer");
			}
			//Minecraft.getMinecraft().player.sendChatMessage(time + " -- " + buffer + " -- " + owner + " -- " + thief);
		}

		CapManager manager = new CapManager(entityIn);
		manager.addMana(manager.getMaxMana() / 1000);
		manager.removeBurnout(manager.getMaxBurnout() / 1000);

		if (manager.getMaxMana() < 100)
			manager.setMaxMana(100);
		if (manager.getMaxBurnout() < 100)
			manager.setMaxBurnout(100);

		if (!stack.isEmpty() && stack.getItem() == ModItems.CAPE) {
			double x = ItemNBTHelper.getInt(stack, "time", 0) / 1000.0;
			double buffer = (1 - (Math.exp(-x))) * 10000;
			if (buffer < 100) return;
			manager.setMaxMana(buffer);
			manager.setMaxBurnout(buffer);
		}
	}
}
