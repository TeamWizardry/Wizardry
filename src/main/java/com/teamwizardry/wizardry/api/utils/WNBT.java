package com.teamwizardry.wizardry.api.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

public class WNBT {

    public static float getFloat(CompoundNBT tag, String key, float defaultValue) {
        return tag.contains(key, Constants.NBT.TAG_FLOAT) ? tag.getFloat(key) : defaultValue;
    }

    public static int getInt(CompoundNBT tag, String key, int defaultValue) {
        return tag.contains(key, Constants.NBT.TAG_INT) ? tag.getInt(key) : defaultValue;
    }

    public static double getDouble(CompoundNBT tag, String key, double defaultValue) {
        return tag.contains(key, Constants.NBT.TAG_DOUBLE) ? tag.getDouble(key) : defaultValue;
    }

    public static String getString(CompoundNBT tag, String key, String defaultValue) {
        return tag.contains(key, Constants.NBT.TAG_STRING) ? tag.getString(key) : defaultValue;
    }

    public static boolean getBoolean(CompoundNBT tag, String key, boolean defaultValue) {
        return tag.contains(key, Constants.NBT.TAG_BYTE) ? tag.getBoolean(key) : defaultValue;
    }

    public static long getLong(CompoundNBT tag, String key, long defaultValue) {
        return tag.contains(key, Constants.NBT.TAG_LONG) ? tag.getLong(key) : defaultValue;
    }

    public static float getFloat(ItemStack stack, String key, float defaultValue) {
        CompoundNBT tag = stack.getOrCreateTag();
        return getFloat(tag, key, defaultValue);
    }

    public static int getInt(ItemStack stack, String key, int defaultValue) {
        CompoundNBT tag = stack.getOrCreateTag();
        return getInt(tag, key, defaultValue);
    }

    public static double getDouble(ItemStack stack, String key, double defaultValue) {
        CompoundNBT tag = stack.getOrCreateTag();
        return getDouble(tag, key, defaultValue);
    }

    public static String getString(ItemStack stack, String key, String defaultValue) {
        CompoundNBT tag = stack.getOrCreateTag();
        return getString(tag, key, defaultValue);
    }

	public static boolean getBoolean(ItemStack stack, String key, boolean defaultValue) {
        CompoundNBT tag = stack.getOrCreateTag();
        return getBoolean(tag, key, defaultValue);
    }

	public static long getLong(ItemStack stack, String key, long defaultValue) {
        CompoundNBT tag = stack.getOrCreateTag();
        return getLong(tag, key, defaultValue);
    }
}
