package com.teamwizardry.wizardry.common.utils

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement

object WNBT {
    fun NbtCompound.getFloat(key: String, defaultValue: Float): Float {
        return if (this.contains(key, NbtElement.FLOAT_TYPE.toInt())) this.getFloat(key) else defaultValue
    }

    fun NbtCompound.getInt(key: String, defaultValue: Int): Int {
        return if (this.contains(key, NbtElement.INT_TYPE.toInt())) this.getInt(key) else defaultValue
    }

    fun NbtCompound.getDouble(key: String, defaultValue: Double): Double {
        return if (this.contains(key, NbtElement.DOUBLE_TYPE.toInt())) this.getDouble(key) else defaultValue
    }

    fun NbtCompound.getString(key: String, defaultValue: String): String {
        return if (this.contains(key, NbtElement.STRING_TYPE.toInt())) this.getString(key) else defaultValue
    }

    fun NbtCompound.getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (this.contains(key, NbtElement.BYTE_TYPE.toInt())) this.getBoolean(key) else defaultValue
    }

    fun NbtCompound.getLong(key: String, defaultValue: Long): Long {
        return if (this.contains(key, NbtElement.LONG_TYPE.toInt())) this.getLong(key) else defaultValue
    }

    fun ItemStack.getFloat(key: String, defaultValue: Float): Float {
        return this.getOrCreateNbt().getFloat(key, defaultValue)
    }

    fun ItemStack.getInt(key: String, defaultValue: Int): Int {
        return this.getOrCreateNbt().getInt(key, defaultValue)
    }

    fun ItemStack.getDouble(key: String, defaultValue: Double): Double {
        return this.getOrCreateNbt().getDouble(key, defaultValue)
    }

    fun ItemStack.getString(key: String, defaultValue: String): String {
        return this.getOrCreateNbt().getString(key, defaultValue)
    }

    fun ItemStack.getBoolean(key: String, defaultValue: Boolean): Boolean {
        return this.getOrCreateNbt().getBoolean(key, defaultValue)
    }

    fun ItemStack.getLong(stack: ItemStack, key: String, defaultValue: Long): Long {
        return this.getOrCreateNbt().getLong(key, defaultValue)
    }
}