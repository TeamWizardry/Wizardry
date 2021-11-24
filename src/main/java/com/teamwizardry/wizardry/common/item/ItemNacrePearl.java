package com.teamwizardry.wizardry.common.item;

import org.jetbrains.annotations.NotNull;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemNacrePearl extends Item implements INacreProduct {

	public ItemNacrePearl(Settings settings) {
		super(settings);
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		colorableOnUpdate(stack, worldIn);

		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	// TODO - find item entity version of this
//	@Override
//	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
//		colorableOnEntityItemUpdate(entity);
//
//		return super.onEntityItemUpdate(stack, entity);
//	}

	private String getNameType(@NotNull ItemStack stack) {
		float quality = this.getQuality(stack);
		if (quality > 1)
			return "ancient";
		else if (quality == 1)
			return "apex";
		else if (quality > 0.8)
			return "potent";
		else if (quality > 0.6)
			return "decent";
		else if (quality > 0.4)
			return "flawed";
		else if (quality > 0.2)
			return "drained";
		return "wasted";
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (!stack.hasNbt())
			return super.getTranslationKey(stack);
		return super.getTranslationKey(stack) + "." + getNameType(stack);
	}
}
