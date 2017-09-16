package com.teamwizardry.wizardry.common.core;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.ImmutableList;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

public final class BaublesSupport {
	private BaublesSupport() {}

	private static final class ArmorHolder {
		private static final FallbackArmorAccessor ACCESSOR = new ArmorAccessor();
	}

	public static ItemStack getCape(EntityPlayer player) {
		for (ItemStack stack : getArmor(player)) {
			if (stack.getItem() == ModItems.CAPE) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	public static Iterable<ItemStack> getArmor(EntityPlayer player) {
		return ArmorHolder.ACCESSOR.get(player);
	}

	private static class FallbackArmorAccessor {
		public Iterable<ItemStack> get(EntityPlayer player) {
			return player.getArmorInventoryList();
		}
	}

	private static final class ArmorAccessor extends FallbackArmorAccessor {
		@Override
		@Optional.Method(modid = "baubles")
		public Iterable<ItemStack> get(EntityPlayer player) {
			ImmutableList.Builder<ItemStack> stacks = ImmutableList.builder();
			IBaublesItemHandler inv = BaublesApi.getBaublesHandler(player);
			for (int slot : BaubleType.BODY.getValidSlots()) {
				stacks.add(inv.getStackInSlot(slot));
			}
			return stacks/*.addAll(super.get(player))*/.build();
		}
	}
}
