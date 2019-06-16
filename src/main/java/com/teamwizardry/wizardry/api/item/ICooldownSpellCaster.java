package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
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

public interface ICooldownSpellCaster {

	default void setCooldown(World world, @Nullable EntityPlayer player, @Nullable EnumHand hand, ItemStack stack, @Nonnull SpellData data) {
		int maxCooldown = 0;

		List<SpellRing> rings = SpellUtils.getAllSpellRings(stack);

		for (SpellRing ring : rings) {
			if (ring.isContinuous()) return;

			if (ring.getModule() != null && ring.getModule().getModuleClass() instanceof IOverrideCooldown) {
				maxCooldown = ((IOverrideCooldown) ring.getModule().getModuleClass()).getNewCooldown(world, data, ring);
				break;
			}
			if (ring.getCooldownTime() > maxCooldown)
				maxCooldown = ring.getCooldownTime();
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

		NBTHelper.setInt(stack, Constants.NBT.LAST_COOLDOWN, maxCooldown);
		NBTHelper.setLong(stack, Constants.NBT.LAST_CAST, world.getTotalWorldTime());
	}

	default boolean isCoolingDown(World world, ItemStack stack) {
		int lastCooldown = NBTHelper.getInt(stack, Constants.NBT.LAST_COOLDOWN, 0);
		long lastCast = NBTHelper.getLong(stack, Constants.NBT.LAST_CAST, 0);
		long currentCast = world.getTotalWorldTime();

		long sub = currentCast - lastCast;
		return sub <= lastCooldown;
	}
}
