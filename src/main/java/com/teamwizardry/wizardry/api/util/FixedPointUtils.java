package com.teamwizardry.wizardry.api.util;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Helper class for to convert between fixed point and float point value storage. 
 * 
 * @author Avatair
 */
public class FixedPointUtils {
	/**
	 * <b>NOTE</b>: Best to be a power of two.
	 */
	// TODO: Move to some suitable constants class
	public static final double NBT_FIXEDPOINT_GRANULARITY = 2048;
	
	private FixedPointUtils() {}

	public static double fixedToDouble(long fixedVal) {
		return (double)fixedVal / NBT_FIXEDPOINT_GRANULARITY;
	}
	
	public static long doubleToFixed(double dblVal) {
		return (long)(dblVal * NBT_FIXEDPOINT_GRANULARITY);
	}
	
	public static double getDoubleFromNBT(NBTTagCompound nbt, String key) {
		// See net.minecraft.nbt.NBTBase.NBT_TYPES[] for meaning of type ids.
		
		byte dataType = nbt.getTagId(key);
		if( dataType == 5 || dataType == 6 )
			return nbt.getDouble(key);	// NOTE: For legacy case
		else if( dataType == 1 || dataType == 2 || dataType == 3 || dataType == 4 )
			return FixedPointUtils.fixedToDouble(nbt.getLong(key));
		return 0;
	}
	
	public static long getFixedFromNBT(NBTTagCompound nbt, String key) {
		// See net.minecraft.nbt.NBTBase.NBT_TYPES[] for meaning of type ids.
		
		byte dataType = nbt.getTagId(key);
		if( dataType == 5 || dataType == 6 )
			return FixedPointUtils.doubleToFixed(nbt.getDouble(key));	// NOTE: For legacy case
		else if( dataType == 1 || dataType == 2 || dataType == 3 || dataType == 4 )
			return nbt.getLong(key);
		return 0;		
	}
	
	public static void setDoubleToNBT(NBTTagCompound nbt, String key, double value) {
		nbt.setLong(key, FixedPointUtils.doubleToFixed(value));
	}
	
	public static void setFixedToNBT(NBTTagCompound nbt, String key, long value) {
		nbt.setLong(key, value);
	}
}
