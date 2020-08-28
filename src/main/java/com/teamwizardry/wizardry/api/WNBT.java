package com.teamwizardry.wizardry.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class WNBT {

	public static float getFloat(ItemStack stack, String key, float defaultValue) {
		CompoundNBT tag = stack.getOrCreateTag();
		return tag.contains(key, Constants.NBT.TAG_FLOAT) ? tag.getFloat(key) : defaultValue;
	}

	public static int getInt(ItemStack stack, String key, int defaultValue) {
		CompoundNBT tag = stack.getOrCreateTag();
		return tag.contains(key, Constants.NBT.TAG_INT) ? tag.getInt(key) : defaultValue;

	}

	public static double getDouble(ItemStack stack, String key, double defaultValue) {
		CompoundNBT tag = stack.getOrCreateTag();
		return tag.contains(key, Constants.NBT.TAG_DOUBLE) ? tag.getDouble(key) : defaultValue;
	}

	public static String getString(ItemStack stack, String key, String defaultValue) {
		CompoundNBT tag = stack.getOrCreateTag();
		return tag.contains(key, Constants.NBT.TAG_STRING) ? tag.getString(key) : defaultValue;
	}

	public static boolean getBoolean(ItemStack stack, String key, boolean defaultValue) {
		CompoundNBT tag = stack.getOrCreateTag();
		return tag.contains(key, Constants.NBT.TAG_BYTE) ? tag.getBoolean(key) : defaultValue;
	}

	public static long getLong(ItemStack stack, String key, long defaultValue) {
		CompoundNBT tag = stack.getOrCreateTag();
		return tag.contains(key, Constants.NBT.TAG_LONG) ? tag.getLong(key) : defaultValue;
	}
}
