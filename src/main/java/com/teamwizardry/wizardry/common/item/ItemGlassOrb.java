package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

/**
 * Created by Saad on 6/20/2016.
 */
public class ItemGlassOrb extends ItemWizardry implements Explodable {

	public ItemGlassOrb() {
		super("glass_orb");
		setMaxStackSize(1);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.isInsideOfMaterial(ModBlocks.NACRE_MATERIAL)) {
			ItemStack newStack = new ItemStack(ModItems.PEARL_NACRE);
			entityItem.setEntityItemStack(newStack);
		} else if (entityItem.isInsideOfMaterial(ModBlocks.MANA_MATERIAL)) {
			ItemStack newStack = new ItemStack(ModItems.MANA_ORB);
			entityItem.setEntityItemStack(newStack);
		}
		return super.onEntityItemUpdate(entityItem);
	}
}
