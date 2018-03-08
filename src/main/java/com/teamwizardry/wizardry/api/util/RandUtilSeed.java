package com.teamwizardry.wizardry.api.util;

import java.util.Random;

/**
 * Created by Demoniaque.
 */
public class RandUtilSeed {

	public Random random;

	public RandUtilSeed(long seed) {
		random = new Random(seed);
	}

	public double nextDouble(double min, double max) {
		return (random.nextDouble() * (max - min)) + min;
	}

	public double nextDouble(double bound) {
		return (random.nextDouble() * (bound));
	}

	public double nextDouble() {
		return random.nextDouble();
	}

	public float nextFloat(float min, float max) {
		return (random.nextFloat() * (max - min)) + min;
	}

	public float nextFloat(float bound) {
		return (random.nextFloat() * (bound));
	}

	public float nextFloat() {
		return random.nextFloat();
	}

	public int nextInt(int min, int max) {
		return (int) ((random.nextDouble() * (max - min)) + min);
	}

	public int nextInt(int bound) {
		return (int) (random.nextDouble() * bound);
	}

	public int nextInt() {
		return (int) random.nextDouble();
	}

	public boolean nextBoolean() {
		return random.nextBoolean();
	}
}
