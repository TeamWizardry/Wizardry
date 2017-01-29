package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.common.item.ItemWizardry;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

/**
 * Created by Saad on 6/20/2016.
 */
public class ItemGlassPearl extends ItemWizardry implements Explodable {

	public ItemGlassPearl() {
		super("glass_pearl");
		setMaxStackSize(1);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.isInsideOfMaterial(ModBlocks.NACRE_MATERIAL)) {
			ItemStack newStack = new ItemStack(ModItems.PEARL_NACRE);
			entityItem.setEntityItemStack(newStack);
		} else if (entityItem.isInsideOfMaterial(ModBlocks.MANA_MATERIAL)) {
			ItemStack newStack = new ItemStack(ModItems.PEARL_MANA);
			entityItem.setEntityItemStack(newStack);
		}
		return super.onEntityItemUpdate(entityItem);
	}
}
