package com.teamwizardry.wizardry.api.item;

import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.NBTConstants.NBT;
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
 * Implement this to make your item change it's color light nacre products.
 * Nacre Pearls use this.
 */
public interface INacreProduct extends IItemColorProvider {

	float curveConst = 0.75F / (1.0F - 1 / (float) Math.E);

	default void colorableOnUpdate(ItemStack stack, World world) {
		if (!world.isRemote) {
			if (!NBTHelper.hasNBTEntry(stack, NBT.RAND))
				NBTHelper.setFloat(stack, NBT.RAND, (world.getTotalWorldTime() / 140f) % 140f);

			if (!NBTHelper.hasNBTEntry(stack, NBT.PURITY)) {
				NBTHelper.setInt(stack, NBT.PURITY, NBT.NACRE_PURITY_CONVERSION);
				NBTHelper.setFloat(stack, NBT.PURITY_OVERRIDE, 1f);
			}

			if (!NBTHelper.getBoolean(stack, NBT.COMPLETE, false))
				NBTHelper.setBoolean(stack, NBT.COMPLETE, true);
		}
	}

	default void colorableOnEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.world.isRemote) return;
		ItemStack stack = entityItem.getItem();

		if (!NBTHelper.hasNBTEntry(stack, NBT.RAND))
			NBTHelper.setFloat(stack, NBT.RAND, entityItem.world.rand.nextFloat());

		IBlockState state = entityItem.world.getBlockState(entityItem.getPosition());

		if (state.getBlock() == ModFluids.NACRE.getActualBlock() && !NBTHelper.getBoolean(stack, NBT.COMPLETE, false)) {
			int purity = NBTHelper.getInt(stack, NBT.PURITY, 0);
			purity = Math.min(purity + 1, NBT.NACRE_PURITY_CONVERSION * 2);
			NBTHelper.setInt(stack, NBT.PURITY, purity);
		} else if (NBTHelper.getInt(stack, NBT.PURITY, 0) > 0)
			NBTHelper.setBoolean(stack, NBT.COMPLETE, true);
	}

	default float getQuality(ItemStack stack) {
		if (!stack.hasTagCompound())
			return 1f;
		float override = NBTHelper.getFloat(stack, NBT.PURITY_OVERRIDE, -1f);
		if (override > 0)
			return override;

		float timeConstant = NBT.NACRE_PURITY_CONVERSION;
		int purity = NBTHelper.getInt(stack, NBT.PURITY, NBT.NACRE_PURITY_CONVERSION);
		if (purity > NBT.NACRE_PURITY_CONVERSION + 1)
			return Math.max(0, 2f - purity / timeConstant);
		else if (purity < NBT.NACRE_PURITY_CONVERSION - 1)
			return Math.max(0, purity / timeConstant);
		else
			return 1f;
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	default Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> {
			//float hue = NBTHelper.getFloat(stack, "hue", 0);
			//float sat = NBTHelper.getFloat(stack, "saturation", 0);
			//float pow = Math.min(1f, Math.max(0f, getQuality(stack)));

			//float saturation = curveConst * (1 - (float) Math.pow(Math.E, -pow)) * sat;

			if (tintIndex != 0) return 0xFFFFFF;
			float rand = NBTHelper.getFloat(stack, NBT.RAND, -1);
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

				long lastCast = NBTHelper.getLong(stack, NBT.LAST_CAST, -1);
				int decayCooldown = NBTHelper.getInt(stack, NBT.LAST_COOLDOWN, -1);
				long tick = Minecraft.getMinecraft().world.getTotalWorldTime();
				long timeSinceCooldown = tick - lastCast;
				float decayStage = (decayCooldown > 0) ? ((float) timeSinceCooldown) / decayCooldown : 1f;


				float rand = NBTHelper.getFloat(stack, NBT.RAND, -1);
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
