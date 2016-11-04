package com.teamwizardry.wizardry.client.cloth;

public class MathUtil {

	public static double round(double value, double increment){
		return Math.round(value/increment) * increment;
	}
	public static float round(float value, float increment){
		return Math.round(value/increment) * increment;
	}
	public static int round(int value, int increment){
		return Math.round((float)value/ increment) * increment;
	}

	// ========

	public static double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(max, val));
	}
	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}
	public static int clamp(int val, int min, int max) {
		return Math.max(min, Math.min(max, val));
	}

	// ========

	public static boolean isLessThanOthers(double check, double... others) {
		for (double other : others) {
			if(!( check < other ))
				return false;
		}
		return true;
	}

	public static boolean isLequalToOthers(double check, double... others) {
		for (double other : others) {
			if(!( check <= other ))
				return false;
		}
		return true;
	}

	public static boolean isGreaterThanOthers(double check, double... others) {
		for (double other : others) {
			if(!( check > other ))
				return false;
		}
		return true;
	}

	public static boolean isGequalToOthers(double check, double... others) {
		for (double other : others) {
			if(!( check >= other ))
				return false;
		}
		return true;
	}
}