package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by Saad on 6/7/2016.
 */
public interface INacreColorable extends IItemColorProvider {

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

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	default IItemColor getItemColor() {
		return (stack, tintIndex) -> {
			if (tintIndex != 0) return 0xFFFFFF;
			int rand = 0;
			float saturation = 1f;
			NBTTagCompound compound = stack.getTagCompound();
			if (compound != null && compound.hasKey(TAG_RAND))
				rand = compound.getInteger(TAG_RAND);
			if (compound != null && compound.hasKey(TAG_PURITY))
				saturation = MathHelper.sin(compound.getInteger(TAG_PURITY) * (float) Math.PI * 0.5f / NACRE_PURITY_CONVERSION);

			return Color.HSBtoRGB((rand + ClientTickHandler.getTicksInGame()) / (float) COLOR_CYCLE_LENGTH, saturation * 0.3f, 1f);
		};
	}
}
