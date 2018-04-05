package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.IOverrideCooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.common.network.PacketSyncCooldown;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface ICooldown {

	default void setCooldown(World world, @Nullable EntityPlayer player, @Nullable EnumHand hand, ItemStack stack, @Nonnull SpellData data) {
		int maxCooldown = 0;
		int overridenCooldown = 0;

		boolean cooldownOverriden = false;
		boolean hasNonContinuous = false;

		List<SpellRing> rings = SpellUtils.getAllSpellRings(stack);

		for (SpellRing ring : rings) {
			if (!ring.isContinuous()) {
				hasNonContinuous = true;
			} else continue;
			if (ring.getCooldownTime() > maxCooldown) maxCooldown = ring.getCooldownTime();
		}

		for (SpellRing ring : SpellUtils.getAllSpellRings(stack)) {
			if (ring.getModule() instanceof IOverrideCooldown) {
				int cooldown = ring.getCooldownTime(data);
				if (cooldown > overridenCooldown) overridenCooldown = cooldown;
				cooldownOverriden = true;
			}
		}

		if (!hasNonContinuous) return;

		int finalCooldown = cooldownOverriden ? overridenCooldown : maxCooldown;

		ItemNBTHelper.setInt(stack, "cooldown_ticks", finalCooldown);
		ItemNBTHelper.setInt(stack, Constants.NBT.LAST_COOLDOWN, finalCooldown);
		ItemNBTHelper.setLong(stack, Constants.NBT.LAST_CAST, world.getTotalWorldTime());

		if (player != null && hand != null)
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
