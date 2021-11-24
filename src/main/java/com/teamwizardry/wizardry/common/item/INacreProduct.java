package com.teamwizardry.wizardry.common.item;

import java.awt.Color;

import com.teamwizardry.wizardry.common.utils.WNBT;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public interface INacreProduct extends ItemColorProvider {

    public static final String COMPLETE = "complete";
    public static final String LAST_CAST = "last_cast";
    public static final String LAST_COOLDOWN = "last_cooldown";
    public static final int NACRE_PURITY_CONVERSION = 30*20;
    public static final String PURITY = "purity";
    public static final String PURITY_OVERRIDE = "purity_override";
    public static final String RAND = "rand";
    
	float curveConst = 0.75F / (1.0F - 1 / (float) Math.E);

	default void colorableOnUpdate(ItemStack stack, World world) {
		if (!world.isClient) {
			NbtCompound tag = stack.getOrCreateNbt();

			if (!tag.contains(RAND))
				tag.putFloat(RAND, (world.getTime() / 140f) % 140f);

			if (!tag.contains(PURITY)) {
				tag.putInt(PURITY, NACRE_PURITY_CONVERSION);
				tag.putFloat(PURITY_OVERRIDE, 1f);
			}
			if (!tag.contains(COMPLETE) || !tag.getBoolean(COMPLETE)) {
				tag.putBoolean(COMPLETE, true);
			}
		}
	}

	default void colorableOnEntityItemUpdate(ItemEntity entityItem) {
		if (entityItem.world.isClient) return;
		ItemStack stack = entityItem.getStack();
		NbtCompound tag = stack.getOrCreateNbt();

		if (!tag.contains(RAND))
			tag.putFloat(RAND, entityItem.world.random.nextFloat());

//		BlockState state = entityItem.world.getBlockState(entityItem.getBlockPos());

		// TODO: ModFluids.NACRE
		/*
		if (state.getBlock() == ModFluids.NACRE.getActualBlock() && !WNBT.getBoolean(stack, COMPLETE, false)) {
			int purity = WNBT.getInt(stack, PURITY, 0);
			purity = Math.min(purity + 1, NACRE_PURITY_CONVERSION * 2);
			WNBT.setInt(stack, PURITY, purity);
		} else if (WNBT.getInt(stack, PURITY, 0) > 0)
			WNBT.setBoolean(stack, COMPLETE, true);
		 */
	}

	default float getQuality(ItemStack stack) {
		NbtCompound tag = stack.getOrCreateNbt();
		float override = tag.contains(PURITY_OVERRIDE) ? tag.getFloat(PURITY_OVERRIDE) : 0;
		if (override > 0)
			return override;

		float timeConstant = NACRE_PURITY_CONVERSION;
		int purity = WNBT.getInt(stack, PURITY, NACRE_PURITY_CONVERSION);
		if (purity > NACRE_PURITY_CONVERSION + 1)
			return Math.max(0, 2f - purity / timeConstant);
		else if (purity < NACRE_PURITY_CONVERSION - 1)
			return Math.max(0, purity / timeConstant);
		else
			return 1f;
	}


	@Override
	default int getColor(ItemStack stack, int tintIndex) {
		if (tintIndex != 0) return 0xFFFFFF;
		float rand = WNBT.getFloat(stack, RAND, -1);
		float hue = 0;
		if (MinecraftClient.getInstance().world != null) {
			hue = rand < 0 ? (MinecraftClient.getInstance().world.getTime() / 140f) % 140f : rand;
		}
		float pow = Math.min(1f, Math.max(0f, getQuality(stack)));

		float saturation = curveConst * (1 - (float) Math.pow(Math.E, -pow));

		return Color.HSBtoRGB(hue, saturation, 1f);

	}

	interface INacreDecayProduct extends INacreProduct {
		double decayCurveDelimiter = 1 / 6.0;

		@Override
		default int getColor(ItemStack stack, int tintIndex) {
			if (tintIndex != 0) return 0xFFFFFF;

			long lastCast = WNBT.getLong(stack, LAST_CAST, -1);
			int decayCooldown = WNBT.getInt(stack, LAST_COOLDOWN, -1);
			long tick = 0;
			if (MinecraftClient.getInstance().world != null) {
				tick = MinecraftClient.getInstance().world.getTime();
			}
			long timeSinceCooldown = tick - lastCast;
			float decayStage = (decayCooldown > 0) ? ((float) timeSinceCooldown) / decayCooldown : 1f;


			float rand = WNBT.getFloat(stack, RAND, -1);
			float hue = rand < 0 ? (tick / 140f) % 140f : rand;
			float pow = Math.min(1f, Math.max(0f, getQuality(stack)));

			double decaySaturation = (lastCast == -1 || decayCooldown <= 0 || decayStage >= 1f) ? 1f :
					(decayStage < decayCurveDelimiter) ? Math.pow(Math.E, -15 * decayStage) : Math.pow(Math.E, 3 * decayStage - 3);

			float saturation = curveConst * (1 - (float) Math.pow(Math.E, -pow)) * (float) decaySaturation;

			return Color.HSBtoRGB(hue, saturation, 1f);
		}
	}
}
