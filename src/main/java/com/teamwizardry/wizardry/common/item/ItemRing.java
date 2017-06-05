package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.item.INacreColorable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/13/2016.
 */
public class ItemRing extends ItemWizardry implements INacreColorable {

	public ItemRing() {
		super("ring", "ring", "ring_pearl");
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		colorableOnUpdate(stack, worldIn);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (!entityItem.world.isRemote) return false;

		colorableOnEntityItemUpdate(entityItem);

		return super.onEntityItemUpdate(entityItem);
	}
}
