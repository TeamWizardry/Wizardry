package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.init.ModBlocks;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Random;

/**
 * Created by Saad on 6/7/2016.
 */
public interface INacreColorable extends IItemColorProvider {

	default void colorableOnUpdate(ItemStack stack, World world) {
		if (!world.isRemote) {
			if (!ItemNBTHelper.verifyExistence(stack, NBT.RAND))
				ItemNBTHelper.setInt(stack, NBT.RAND, world.rand.nextInt(Integer.MAX_VALUE));

			if (!ItemNBTHelper.verifyExistence(stack, NBT.PURITY))
				ItemNBTHelper.setInt(stack, NBT.PURITY, NBT.NACRE_PURITY_CONVERSION);

			if (!ItemNBTHelper.getBoolean(stack, NBT.COMPLETE, false))
				ItemNBTHelper.setBoolean(stack, NBT.COMPLETE, true);
		}
	}

	default void colorableOnEntityItemUpdate(EntityItem entityItem) {
		ItemStack stack = entityItem.getEntityItem();

		if (!ItemNBTHelper.verifyExistence(stack, NBT.RAND))
			ItemNBTHelper.setInt(stack, NBT.RAND, entityItem.world.rand.nextInt(Integer.MAX_VALUE));

		if (entityItem.isInsideOfMaterial(ModBlocks.NACRE_MATERIAL) && !ItemNBTHelper.getBoolean(stack, NBT.COMPLETE, false)) {
			int purity = ItemNBTHelper.getInt(stack, NBT.PURITY, 0);
			purity = Math.min(purity + 1, NBT.NACRE_PURITY_CONVERSION * 2);
			ItemNBTHelper.setInt(stack, NBT.PURITY, purity);
		} else ItemNBTHelper.setBoolean(stack, NBT.COMPLETE, true);
	}

	float curveConst = 0.75F / (1.0F - 1 / (float) Math.E);

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	default Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> {
			if (tintIndex != 0) return 0xFFFFFF;
			int rand = ItemNBTHelper.getInt(stack, NBT.RAND, -1);
			float hue = rand == -1 ? MathHelper.sin(Minecraft.getMinecraft().world.getTotalWorldTime() / 140f) : new Random(rand).nextFloat();
			int purity = ItemNBTHelper.getInt(stack, NBT.PURITY, NBT.NACRE_PURITY_CONVERSION);
			float pow = purity / (float) NBT.NACRE_PURITY_CONVERSION;
			if (purity > NBT.NACRE_PURITY_CONVERSION) pow = 1 - pow;

			float saturation = curveConst * (1 - (float) Math.pow(Math.E, -pow));

			return Color.HSBtoRGB(hue, saturation, 1f);
		};
	}
}
