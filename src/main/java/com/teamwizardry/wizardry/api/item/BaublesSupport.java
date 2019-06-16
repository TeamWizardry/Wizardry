package com.teamwizardry.wizardry.api.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.ImmutableList;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public final class BaublesSupport {

	@Nonnull
	public static <T> ItemStack getItem(EntityLivingBase entity, Class<T> clazz) {
		for (ItemStack stack : getArmor(entity)) {
			if (!stack.isEmpty() && clazz.isInstance(stack.getItem())) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	public static ItemStack getItem(EntityLivingBase entity, Item item) {
		for (ItemStack stack : getArmor(entity)) {
			if (stack.getItem() == item) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	public static ItemStack getItem(EntityLivingBase entity, Item... items) {
		for (ItemStack stack : getArmor(entity)) {
			for (Item item : items)
				if (stack.getItem() == item) {
					return stack;
				}
		}
		return ItemStack.EMPTY;
	}

	public static List<ItemStack> getAllBaubles(EntityLivingBase entity) {
		return ArmorHolder.ACCESSOR.getBaublesOnly(entity);
	}

	public static boolean isBauble(ItemStack stack) {
		return StackHolder.ACCESSOR.get(stack);
	}

	public static Iterable<ItemStack> getArmor(EntityLivingBase entity) {
		return ArmorHolder.ACCESSOR.getBaublesFallbackArmor(entity);
	}

	private static final class StackHolder {
		private static final FallbackStackAccessor ACCESSOR = new StackAccessor();
	}

	private static final class ArmorHolder {
		private static final FallbackArmorAccessor ACCESSOR = new ArmorAccessor();
	}

	private static class FallbackArmorAccessor {
		public Iterable<ItemStack> getBaublesFallbackArmor(EntityLivingBase entity) {
			return entity.getArmorInventoryList();
		}

		public List<ItemStack> getBaublesOnly(EntityLivingBase entity) {
			return new ArrayList<>();
		}
	}

	private static class FallbackStackAccessor {
		public boolean get(ItemStack stack) {
			return false;
		}
	}

	private static final class ArmorAccessor extends FallbackArmorAccessor {
		@Override
		@Optional.Method(modid = "baubles")
		public Iterable<ItemStack> getBaublesFallbackArmor(EntityLivingBase entity) {
			if (!(entity instanceof EntityPlayer)) return entity.getArmorInventoryList();
			if (BaublesApi.getBaublesHandler((EntityPlayer) entity) == null) return entity.getArmorInventoryList();

			ImmutableList.Builder<ItemStack> stacks = ImmutableList.builder();
			IBaublesItemHandler inv = BaublesApi.getBaublesHandler((EntityPlayer) entity);
			for (BaubleType type : BaubleType.values())
				for (int slot : type.getValidSlots()) {
					stacks.add(inv.getStackInSlot(slot));
				}
			return stacks.build();
		}

		@Override
		@Optional.Method(modid = "baubles")
		public List<ItemStack> getBaublesOnly(EntityLivingBase entity) {
			if (!(entity instanceof EntityPlayer)) return new ArrayList<>();
			if (BaublesApi.getBaublesHandler((EntityPlayer) entity) == null) return new ArrayList<>();

			ImmutableList.Builder<ItemStack> stacks = ImmutableList.builder();
			IBaublesItemHandler inv = BaublesApi.getBaublesHandler((EntityPlayer) entity);
			for (BaubleType type : BaubleType.values())
				for (int slot : type.getValidSlots()) {
					stacks.add(inv.getStackInSlot(slot));
				}
			return stacks.build();
		}
	}

	private static final class StackAccessor extends FallbackStackAccessor {
		@Override
		@Optional.Method(modid = "baubles")
		public boolean get(ItemStack stack) {
			return stack.getItem() instanceof IBauble;
		}
	}
}
