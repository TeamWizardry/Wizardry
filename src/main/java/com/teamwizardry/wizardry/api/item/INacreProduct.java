package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import kotlin.jvm.functions.Function2;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Demoniaque on 6/7/2016.
 */
public interface INacreProduct extends IItemColorProvider {

	float curveConst = 0.75F / (1.0F - 1 / (float) Math.E);

	default void colorableOnUpdate(ItemStack stack, World world) {
		if (!world.isRemote) {
			if (!ItemNBTHelper.verifyExistence(stack, NBT.RAND))
				ItemNBTHelper.setFloat(stack, NBT.RAND, (world.getTotalWorldTime() / 140f) % 140f);

			if (!ItemNBTHelper.verifyExistence(stack, NBT.PURITY)) {
				ItemNBTHelper.setInt(stack, NBT.PURITY, NBT.NACRE_PURITY_CONVERSION);
				ItemNBTHelper.setFloat(stack, NBT.PURITY_OVERRIDE, 1f);
			}

			if (!ItemNBTHelper.getBoolean(stack, NBT.COMPLETE, false))
				ItemNBTHelper.setBoolean(stack, NBT.COMPLETE, true);
		}
	}

	default void colorableOnEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.world.isRemote) return;
		ItemStack stack = entityItem.getItem();

		if (!ItemNBTHelper.verifyExistence(stack, NBT.RAND))
			ItemNBTHelper.setFloat(stack, NBT.RAND, entityItem.world.rand.nextFloat());

		IBlockState state = entityItem.world.getBlockState(entityItem.getPosition());

		if (state.getBlock() == ModFluids.NACRE.getActualBlock() && !ItemNBTHelper.getBoolean(stack, NBT.COMPLETE, false)) {
			int purity = ItemNBTHelper.getInt(stack, NBT.PURITY, 0);
			purity = Math.min(purity + 1, NBT.NACRE_PURITY_CONVERSION * 2);
			ItemNBTHelper.setInt(stack, NBT.PURITY, purity);
		} else if (ItemNBTHelper.getInt(stack, NBT.PURITY, 0) > 0)
			ItemNBTHelper.setBoolean(stack, NBT.COMPLETE, true);
	}

	default float getQuality(ItemStack stack) {
		if (!stack.hasTagCompound())
			return 1f;
		float override = ItemNBTHelper.getFloat(stack, NBT.PURITY_OVERRIDE, -1f);
		if (override > 0)
			return override;

		int purity = Math.max(0, Math.min(NBT.NACRE_PURITY_CONVERSION * 2, ItemNBTHelper.getInt(stack, NBT.PURITY, NBT.NACRE_PURITY_CONVERSION)));
		float quality = purity / (float) NBT.NACRE_PURITY_CONVERSION;
		if (quality > 1) quality = 2 - quality;

		return quality;
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	default Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> {
			//float hue = ItemNBTHelper.getFloat(stack, "hue", 0);
			//float sat = ItemNBTHelper.getFloat(stack, "saturation", 0);
			//float pow = Math.min(1f, Math.max(0f, getQuality(stack)));

			//float saturation = curveConst * (1 - (float) Math.pow(Math.E, -pow)) * sat;

			if (tintIndex != 0) return 0xFFFFFF;
			float rand = ItemNBTHelper.getFloat(stack, NBT.RAND, -1);
			float hue = rand < 0 ? (Minecraft.getMinecraft().world.getTotalWorldTime() / 140f) % 140f : rand;
			float pow = Math.min(1f, Math.max(0f, getQuality(stack)));

			float saturation = curveConst * (1 - (float) Math.pow(Math.E, -pow));

			return Color.HSBtoRGB(hue, saturation, 1f);
		};
	}

	interface INacreDecayProduct extends INacreProduct {
		double decayCurveDelimiter = 1 / 6.0;

		@Nullable
		@Override
		@SideOnly(Side.CLIENT)
		default Function2<ItemStack, Integer, Integer> getItemColorFunction() {
			return (stack, tintIndex) -> {
				if (tintIndex != 0) return 0xFFFFFF;
				if (!stack.hasTagCompound())
					return Color.HSBtoRGB(MathHelper.sin(Minecraft.getMinecraft().world.getTotalWorldTime() / 140f), 0.75f, 1f);

				long lastCast = ItemNBTHelper.getLong(stack, NBT.LAST_CAST, -1);
				int decayCooldown = ItemNBTHelper.getInt(stack, NBT.LAST_COOLDOWN, -1);
				long tick = Minecraft.getMinecraft().world.getTotalWorldTime();
				long timeSinceCooldown = tick - lastCast;
				float decayStage = (decayCooldown > 0) ? ((float) timeSinceCooldown) / decayCooldown : 1f;


				float rand = ItemNBTHelper.getFloat(stack, NBT.RAND, -1);
				float hue = rand < 0 ? (tick / 140f) % 140f : rand;
				float pow = Math.min(1f, Math.max(0f, getQuality(stack)));

				double decaySaturation = (lastCast == -1 || decayCooldown <= 0 || decayStage >= 1f) ? 1f :
						(decayStage < decayCurveDelimiter) ? Math.pow(Math.E, -15 * decayStage) : Math.pow(Math.E, 3 * decayStage - 3);

				float saturation = curveConst * (1 - (float) Math.pow(Math.E, -pow)) * (float) decaySaturation;

				return Color.HSBtoRGB(hue, saturation, 1f);
			};
		}
	}
}
