package com.teamwizardry.wizardry.api.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

/**
 * Helper class for to convert between fixed point and float point value storage.
 *
 * @author Avatair
 */
public class FixedPointUtils {
	/**
	 * <b>NOTE</b>: Best to be a power of two.
	 */
	public static final float NBT_FIXEDPOINT_GRANULARITY = 2048;

	private FixedPointUtils() {
	}

	public static float fixedToDouble(long fixedVal) {
		return (float) fixedVal / NBT_FIXEDPOINT_GRANULARITY;
	}

	public static float doubleToFixed(double dblVal) {
		return (float) dblVal;
	}

	public static float getDoubleFromNBT(NBTTagCompound nbt, String key) {
		// See net.minecraft.nbt.NBTBase.NBT_TYPES[] for meaning of type ids.

		byte dataType = nbt.getTagId(key);
		if (dataType == Constants.NBT.TAG_DOUBLE) // double
			return (float) nbt.getDouble(key);    // NOTE: For legacy case
		else if (dataType == Constants.NBT.TAG_FLOAT)
			return nbt.getFloat(key);
		else if (dataType == Constants.NBT.TAG_LONG) {
			return fixedToDouble(nbt.getLong(key));
		}
		return 0;
	}

	public static float getFixedFromNBT(NBTTagCompound nbt, String key) {
		// See net.minecraft.nbt.NBTBase.NBT_TYPES[] for meaning of type ids.

		byte dataType = nbt.getTagId(key);
		if (dataType == Constants.NBT.TAG_DOUBLE)
			return (float) nbt.getDouble(key);    // NOTE: For legacy case
		else if (dataType == Constants.NBT.TAG_FLOAT) // byte, short, int, long, float
			return nbt.getFloat(key);
		else if (dataType == Constants.NBT.TAG_LONG)
			return fixedToDouble(nbt.getLong(key));
		return 0;
	}

	public static void setFixedToNBT(NBTTagCompound nbt, String key, float value) {
		nbt.setFloat(key, value);
	}
}
