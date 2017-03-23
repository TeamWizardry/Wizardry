package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.item.Infusable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/28/2016.
 */
public class ItemNacrePearl extends ItemWizardry implements Infusable, Explodable, INacreColorable {

	public ItemNacrePearl() {
		super("nacre_pearl");
		setMaxStackSize(1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote) return;

		colorableOnUpdate(stack);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (!entityItem.world.isRemote) return false;

		colorableOnEntityItemUpdate(entityItem);

		return super.onEntityItemUpdate(entityItem);
	}
}