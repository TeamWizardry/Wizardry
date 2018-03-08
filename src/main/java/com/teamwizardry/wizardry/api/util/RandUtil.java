package com.teamwizardry.wizardry.api.util;

import java.util.Random;

/**
 * Created by Demoniaque.
 */
public class RandUtil {

	public static Random random = new Random();

	public static double nextDouble(double min, double max) {
		return (random.nextDouble() * (max - min)) + min;
	}

	public static double nextDouble(double bound) {
		return (random.nextDouble() * (bound));
	}

	public static long nextDouble() {
		return (long) random.nextDouble();
	}

	public static long nextLong(long min, long max) {
		return (long) ((random.nextDouble() * (max - min)) + min);
	}

	public static long nextLong(long bound) {
		return (long) (random.nextDouble() * (bound));
	}

	public static double nextLong() {
		return random.nextDouble();
	}

	public static float nextFloat(float min, float max) {
		return (random.nextFloat() * (max - min)) + min;
	}

	public static float nextFloat(float bound) {
		return (random.nextFloat() * (bound));
	}

	public static float nextFloat() {
		return random.nextFloat();
	}

	public static int nextInt(int min, int max) {
		return (int) ((random.nextDouble() * (max - min)) + min);
	}

	public static int nextInt(int bound) {
		return (int) (random.nextDouble() * bound);
	}

	public static int nextInt() {
		return (int) random.nextDouble();
	}

	public static boolean nextBoolean() {
		return random.nextBoolean();
	}
}
