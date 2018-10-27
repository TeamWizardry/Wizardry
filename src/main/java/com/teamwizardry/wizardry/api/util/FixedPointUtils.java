package com.teamwizardry.wizardry.api.util;

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
}
