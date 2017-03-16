package com.teamwizardry.wizardry.api.util;

import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by LordSaad.
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

	@NotNull
	@Override
	public InterpFunction<Float> reverse() {
		return null;
	}

	@NotNull
	@Override
	public List<Float> list(int i) {
		return null;
	}
}
