package com.teamwizardry.wizardry.client.cloth;

import net.minecraft.util.math.Vec3d;

public class Geometry {
	public static Vec3d getNormal(Vec3d a, Vec3d b, Vec3d c) {
		Vec3d edge1 = a.subtract(b);
		Vec3d edge2 = b.subtract(c);
		Vec3d cross = edge1.crossProduct(edge2);
		return cross.normalize();
	}
}
