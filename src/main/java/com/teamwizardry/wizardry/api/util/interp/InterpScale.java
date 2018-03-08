package com.teamwizardry.wizardry.api.util.interp;


import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class InterpScale implements InterpFunction<Float> {

	private float start;
	private float finish;

	public InterpScale(float start, float finish) {

		this.start = start;
		this.finish = finish;
	}

	@Override
	public Float get(float v) {
		return Math.abs((start * (1 - v)) + (finish * v));
	}

	@Nonnull
	@Override
	public InterpFunction<Float> reverse() {
		return null;
	}

	@Nonnull
	@Override
	public List<Float> list(int i) {
		return null;
	}
}
