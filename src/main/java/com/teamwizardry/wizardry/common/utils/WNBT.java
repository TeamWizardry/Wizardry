package com.teamwizardry.wizardry.common.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class WNBT {

    public static float getFloat(NbtCompound tag, String key, float defaultValue) {
        return tag.contains(key, NbtElement.FLOAT_TYPE) ? tag.getFloat(key) : defaultValue;
    }

    public static int getInt(NbtCompound tag, String key, int defaultValue) {
        return tag.contains(key, NbtElement.INT_TYPE) ? tag.getInt(key) : defaultValue;
    }

    public static double getDouble(NbtCompound tag, String key, double defaultValue) {
        return tag.contains(key, NbtElement.DOUBLE_TYPE) ? tag.getDouble(key) : defaultValue;
    }

    public static String getString(NbtCompound tag, String key, String defaultValue) {
        return tag.contains(key, NbtElement.STRING_TYPE) ? tag.getString(key) : defaultValue;
    }

    public static boolean getBoolean(NbtCompound tag, String key, boolean defaultValue) {
        return tag.contains(key, NbtElement.BYTE_TYPE) ? tag.getBoolean(key) : defaultValue;
    }

    public static long getLong(NbtCompound tag, String key, long defaultValue) {
        return tag.contains(key, NbtElement.LONG_TYPE) ? tag.getLong(key) : defaultValue;
    }

    public static float getFloat(ItemStack stack, String key, float defaultValue) {
        NbtCompound tag = stack.getOrCreateNbt();
        return getFloat(tag, key, defaultValue);
    }

    public static int getInt(ItemStack stack, String key, int defaultValue) {
        NbtCompound tag = stack.getOrCreateNbt();
        return getInt(tag, key, defaultValue);
    }

    public static double getDouble(ItemStack stack, String key, double defaultValue) {
        NbtCompound tag = stack.getOrCreateNbt();
        return getDouble(tag, key, defaultValue);
    }

    public static String getString(ItemStack stack, String key, String defaultValue) {
        NbtCompound tag = stack.getOrCreateNbt();
        return getString(tag, key, defaultValue);
    }

	public static boolean getBoolean(ItemStack stack, String key, boolean defaultValue) {
        NbtCompound tag = stack.getOrCreateNbt();
        return getBoolean(tag, key, defaultValue);
    }

	public static long getLong(ItemStack stack, String key, long defaultValue) {
        NbtCompound tag = stack.getOrCreateNbt();
        return getLong(tag, key, defaultValue);
    }
}
