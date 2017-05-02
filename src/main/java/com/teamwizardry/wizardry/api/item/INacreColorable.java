package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.init.ModBlocks;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Saad on 6/7/2016.
 */
public interface INacreColorable extends IItemColorProvider {

	default void colorableOnUpdate(ItemStack stack) {
		if (!ItemNBTHelper.verifyExistence(stack, NBT.RAND))
			ItemNBTHelper.setInt(stack, NBT.RAND, 0);
		if (!ItemNBTHelper.verifyExistence(stack, NBT.PURITY))
			ItemNBTHelper.setInt(stack, NBT.PURITY, NBT.NACRE_PURITY_CONVERSION);
		if (!ItemNBTHelper.getBoolean(stack, NBT.COMPLETE, false))
			ItemNBTHelper.setBoolean(stack, NBT.COMPLETE, true);
	}

	default void colorableOnEntityItemUpdate(EntityItem entityItem) {
		ItemStack stack = entityItem.getEntityItem();

		if (!ItemNBTHelper.verifyExistence(stack, NBT.RAND))
			ItemNBTHelper.setInt(stack, NBT.RAND, entityItem.world.rand.nextInt(NBT.COLOR_CYCLE_LENGTH));

		if (entityItem.isInsideOfMaterial(ModBlocks.NACRE_MATERIAL) && !ItemNBTHelper.getBoolean(stack, NBT.COMPLETE, false)) {
			int purity = ItemNBTHelper.getInt(stack, NBT.PURITY, 0);
			purity = Math.min(purity + 1, NBT.NACRE_PURITY_CONVERSION * 2);
			ItemNBTHelper.setInt(stack, NBT.PURITY, purity);
		} else ItemNBTHelper.setBoolean(stack, NBT.COMPLETE, true);
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	default Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> {
			if (tintIndex != 0) return 0xFFFFFF;
			int rand = ItemNBTHelper.getInt(stack, NBT.RAND, 0);
			int purity = ItemNBTHelper.getInt(stack, NBT.PURITY, NBT.NACRE_PURITY_CONVERSION);
			float saturation = MathHelper.sin((purity * (float) Math.PI * 0.5f) / NBT.NACRE_PURITY_CONVERSION);

			return Color.HSBtoRGB((rand + ClientTickHandler.getTicksInGame()) / (float) NBT.COLOR_CYCLE_LENGTH, saturation * 0.3f, 1f);
		};
	}
}
