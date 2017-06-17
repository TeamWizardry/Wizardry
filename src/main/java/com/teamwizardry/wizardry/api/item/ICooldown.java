package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.IContinuousSpell;
import com.teamwizardry.wizardry.api.spell.INullifyCooldown;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import com.teamwizardry.wizardry.common.network.PacketSyncCooldown;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.ArrayList;

public interface ICooldown {

	default void setCooldown(ItemStack stack, EnumHand hand, EntityPlayer player, World world) {
		int maxCooldown = 0;
		int countNulls = 0;
		int countContinuous = 0;
		int existingCooldowns = 0;

		ArrayList<Module> modules = SpellStack.getAllModules(stack);

		for (Module module : modules) {
			existingCooldowns++;
			if (module instanceof IContinuousSpell) {
				countContinuous++;
				continue;
			}
			if (module instanceof INullifyCooldown) {
				countNulls++;
				continue;
			}
			if (module.getCooldownTime() > maxCooldown) maxCooldown = module.getCooldownTime();
		}
		if (countContinuous >= existingCooldowns / 2.0 || countNulls >= existingCooldowns / 2.0) return;

		ItemNBTHelper.setInt(stack, "cooldown_ticks", maxCooldown);
		ItemNBTHelper.setInt(stack, Constants.NBT.LAST_COOLDOWN, maxCooldown);
		ItemNBTHelper.setLong(stack, Constants.NBT.LAST_CAST, world.getTotalWorldTime());

		if (!world.isRemote)
			if (hand == EnumHand.MAIN_HAND)
				PacketHandler.NETWORK.sendTo(new PacketSyncCooldown(true, false), (EntityPlayerMP) player);
			else PacketHandler.NETWORK.sendTo(new PacketSyncCooldown(false, true), (EntityPlayerMP) player);
	}

	default void updateCooldown(ItemStack stack, EntityPlayer player) {
		for (Module module : SpellStack.getAllModules(stack)) if (module instanceof IContinuousSpell) return;

		if (ItemNBTHelper.getInt(stack, "cooldown_ticks", 0) > 0)
			ItemNBTHelper.setInt(stack, "cooldown_ticks", ItemNBTHelper.getInt(stack, "cooldown_ticks", 0) - 1);
		else ItemNBTHelper.removeEntry(stack, "cooldown_ticks");
	}

	default boolean isCoolingDown(ItemStack stack) {
		return ItemNBTHelper.verifyExistence(stack, "cooldown_ticks");
	}
}
