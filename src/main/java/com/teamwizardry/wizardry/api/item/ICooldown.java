package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.IContinuousModule;
import com.teamwizardry.wizardry.api.spell.IOverrideCooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.common.network.PacketSyncCooldown;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public interface ICooldown {

	default void setCooldown(World world, EntityPlayer player, ItemStack stack, EnumHand hand, @Nonnull SpellData data) {
		int maxCooldown = 0;
		int overridenCooldown = 0;

		boolean cooldownOverriden = false;
		boolean hasNonContinuous = false;

		ArrayList<Module> modules = SpellUtils.getModules(stack);

		for (Module module : modules) {
			if (!(module instanceof IContinuousModule)) {
				hasNonContinuous = true;
			} else continue;
			if (module.getCooldownTime() > maxCooldown) maxCooldown = module.getCooldownTime();
		}

		for (Module module : SpellUtils.getAllModules(stack)) {
			if (module instanceof IOverrideCooldown) {
				int cooldown = ((IOverrideCooldown) module).getNewCooldown(data);
				if (cooldown > overridenCooldown) overridenCooldown = cooldown;
				cooldownOverriden = true;
			}
		}

		if (!hasNonContinuous) return;

		int finalCooldown = cooldownOverriden ? overridenCooldown : maxCooldown;

		ItemNBTHelper.setInt(stack, "cooldown_ticks", finalCooldown);
		ItemNBTHelper.setInt(stack, Constants.NBT.LAST_COOLDOWN, finalCooldown);
		ItemNBTHelper.setLong(stack, Constants.NBT.LAST_CAST, world.getTotalWorldTime());

		if (!world.isRemote)
			if (hand == EnumHand.MAIN_HAND)
				PacketHandler.NETWORK.sendTo(new PacketSyncCooldown(true, false), (EntityPlayerMP) player);
			else PacketHandler.NETWORK.sendTo(new PacketSyncCooldown(false, true), (EntityPlayerMP) player);
	}

	default void updateCooldown(ItemStack stack) {
		if (ItemNBTHelper.getInt(stack, "cooldown_ticks", 0) > 0)
			ItemNBTHelper.setInt(stack, "cooldown_ticks", ItemNBTHelper.getInt(stack, "cooldown_ticks", 0) - 1);
		else ItemNBTHelper.removeEntry(stack, "cooldown_ticks");
	}

	default boolean isCoolingDown(ItemStack stack) {
		return ItemNBTHelper.verifyExistence(stack, "cooldown_ticks");
	}
}
