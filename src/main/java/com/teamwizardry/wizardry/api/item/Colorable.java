package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 6/7/2016.
 */
public interface Colorable {

	String TAG_RAND = "rand";
	String TAG_PURITY = "purity";
	String TAG_COMPLETE = "complete";
	int NACRE_PURITY_CONVERSION = 30 * 20; // 30 seconds
	int COLOR_CYCLE_LENGTH = 50 * 20; // 50 seconds

	default void colorableOnUpdate(ItemStack stack) {
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		}

		if (!compound.hasKey(TAG_RAND))
			compound.setInteger(TAG_RAND, 0);
		if (!compound.hasKey(TAG_PURITY))
			compound.setInteger(TAG_PURITY, NACRE_PURITY_CONVERSION);
		if (!compound.getBoolean(TAG_COMPLETE))
			compound.setBoolean(TAG_COMPLETE, true);
	}

	default void colorableOnEntityItemUpdate(EntityItem entityItem) {
		ItemStack stack = entityItem.getEntityItem();
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		}

		if (!compound.hasKey(TAG_RAND))
			compound.setInteger(TAG_RAND, entityItem.worldObj.rand.nextInt(COLOR_CYCLE_LENGTH));

		if (entityItem.isInsideOfMaterial(ModBlocks.NACRE_MATERIAL) && !compound.getBoolean(TAG_COMPLETE)) {
			int purity = 0;
			if (compound.hasKey(TAG_PURITY))
				purity = compound.getInteger(TAG_PURITY);
			purity = Math.min(purity + 1, NACRE_PURITY_CONVERSION * 2);
			compound.setInteger(TAG_PURITY, purity);
		} else compound.setBoolean(TAG_COMPLETE, true);
	}
}
