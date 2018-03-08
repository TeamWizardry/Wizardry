package com.teamwizardry.wizardry.api.util.interp;

import com.teamwizardry.librarianlib.features.math.interpolate.InterpFunction;
import com.teamwizardry.wizardry.api.util.CubicBezier;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class InterpSlowDown implements InterpFunction<Vec3d> {

	private final Vec3d start;
	private final Vec3d finish;

	public InterpSlowDown(Vec3d start, Vec3d finish) {
		this.start = start;
		this.finish = finish;
	}

	@Override
	public Vec3d get(float v) {
		double x = new CubicBezier(0.22f, 0.86f, 0.3f, 0.96f).eval(v);
		return start.subtract(finish).scale(x);
	}

	@Nonnull
	@Override
	public InterpFunction<Vec3d> reverse() {
		return null;
	}

	@Nonnull
	@Override
	public List<Vec3d> list(int i) {
		return null;
	}
}
