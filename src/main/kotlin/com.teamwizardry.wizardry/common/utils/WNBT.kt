package com.teamwizardry.wizardry.common.utils

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement

object WNBT {
    fun getFloat(tag: NbtCompound, key: String?, defaultValue: Float): Float {
        return if (tag.contains(key, NbtElement.FLOAT_TYPE.toInt())) tag.getFloat(key) else defaultValue
    }

    fun getInt(tag: NbtCompound, key: String?, defaultValue: Int): Int {
        return if (tag.contains(key, NbtElement.INT_TYPE.toInt())) tag.getInt(key) else defaultValue
    }

    private fun getDouble(tag: NbtCompound, key: String?, defaultValue: Double): Double {
        return if (tag.contains(key, NbtElement.DOUBLE_TYPE.toInt())) tag.getDouble(key) else defaultValue
    }

    private fun getString(tag: NbtCompound, key: String?, defaultValue: String?): String {
        return if (tag.contains(key, NbtElement.STRING_TYPE.toInt())) tag.getString(key) else defaultValue!!
    }

    private fun getBoolean(tag: NbtCompound, key: String?, defaultValue: Boolean): Boolean {
        return if (tag.contains(key, NbtElement.BYTE_TYPE.toInt())) tag.getBoolean(key) else defaultValue
    }

    fun getLong(tag: NbtCompound, key: String?, defaultValue: Long): Long {
        return if (tag.contains(key, NbtElement.LONG_TYPE.toInt())) tag.getLong(key) else defaultValue
    }

    fun getFloat(stack: ItemStack, key: String?, defaultValue: Float): Float {
        val tag: NbtCompound = stack.orCreateNbt
        return getFloat(tag, key, defaultValue)
    }

    fun getInt(stack: ItemStack, key: String?, defaultValue: Int): Int {
        val tag: NbtCompound = stack.orCreateNbt
        return getInt(tag, key, defaultValue)
    }

    private fun getDouble(stack: ItemStack, key: String?, defaultValue: Double): Double {
        val tag: NbtCompound = stack.orCreateNbt
        return getDouble(tag, key, defaultValue)
    }

    private fun getString(stack: ItemStack, key: String?, defaultValue: String?): String {
        val tag: NbtCompound = stack.orCreateNbt
        return getString(tag, key, defaultValue)
    }

    private fun getBoolean(stack: ItemStack, key: String?, defaultValue: Boolean): Boolean {
        val tag: NbtCompound = stack.orCreateNbt
        return getBoolean(tag, key, defaultValue)
    }

    fun getLong(stack: ItemStack, key: String?, defaultValue: Long): Long {
        val tag: NbtCompound = stack.orCreateNbt
        return getLong(tag, key, defaultValue)
    }
}