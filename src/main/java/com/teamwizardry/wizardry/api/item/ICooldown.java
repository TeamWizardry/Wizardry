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

		List<SpellRing> rings = SpellUtils.getAllSpellRings(stack);

		for (SpellRing ring : rings) {
			if (ring.isContinuous()) return;

			if (ring.getModule() instanceof IOverrideCooldown) {
				maxCooldown = ring.getCooldownTime();
				break;
			}

			maxCooldown += ring.getCooldownTime();
		}

		if (maxCooldown <= 0) return;

		if (player != null && hand != null) {
			player.stopActiveHand();
			player.swingArm(hand);
			if (!world.isRemote)
				if (hand == EnumHand.MAIN_HAND)
					PacketHandler.NETWORK.sendTo(new PacketSyncCooldown(true, false), (EntityPlayerMP) player);
				else PacketHandler.NETWORK.sendTo(new PacketSyncCooldown(false, true), (EntityPlayerMP) player);
		}

		ItemNBTHelper.setInt(stack, Constants.NBT.LAST_COOLDOWN, maxCooldown);
		ItemNBTHelper.setLong(stack, Constants.NBT.LAST_CAST, world.getTotalWorldTime());
	}

	default boolean isCoolingDown(World world, ItemStack stack) {
		int lastCooldown = ItemNBTHelper.getInt(stack, Constants.NBT.LAST_COOLDOWN, 0);
		long lastCast = ItemNBTHelper.getLong(stack, Constants.NBT.LAST_CAST, 0);
		long currentCast = world.getTotalWorldTime();

		return currentCast - lastCast <= lastCooldown;
	}
}
