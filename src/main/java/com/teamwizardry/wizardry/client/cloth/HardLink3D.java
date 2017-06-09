package com.teamwizardry.wizardry.client.cloth;

import net.minecraft.util.math.Vec3d;

public class HardLink3D extends Link3D {

	public HardLink3D(PointMass3D a, PointMass3D b, float strength) {
		super(a, b, strength);
	}

	public void resolve() {
		if (b.pin) return;

		if (a.pos == null || b.pos == null) return;

		Vec3d posDiff = a.pos.subtract(b.pos);
		double d = posDiff.lengthVector();

		double difference = (distance - d) / d;

		Vec3d translate = posDiff.scale(difference);

		b.pos = b.pos.subtract(translate);
	}

}
