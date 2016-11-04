package com.teamwizardry.wizardry.client.cloth;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class Sphere {

	Vec3d pos;
	double radius;

	public Sphere(Vec3d pos, double r) {
		this.pos = pos;
		radius = r;
	}

	public Vec3d trace(Vec3d start, Vec3d end) {

		Vec3d u = end.subtract(start);
		Vec3d v = pos.subtract(start);
		double b = 2 * (v.dotProduct(u));
		double c = v.dotProduct(v) - (radius * radius);
		double discriminant = (b * b) - (4 * c);

		if(discriminant < 0) return end;

		double tMinus = (-b - Math.sqrt(discriminant)) / 2;
		double tPlus = (-b + Math.sqrt(discriminant)) / 2;

		if((tMinus < 0) && (tPlus < 0)) {
			// sphere is behind the ray
			return end;
		}

		double tValue;
		Vec3d normal;
		Vec3d intersection;
		boolean incoming;
		if((tMinus < 0) && (tPlus > 0)) {
			// ray origin lies inside the sphere. take tPlus
			tValue = tPlus;
//			return null;
			intersection = start.add(u.scale(tValue));
			normal = pos.subtract(intersection);

			return end; // ignore outgoing intersections
		} else {
			// both roots positive. take tMinus
			tValue = tMinus;
			intersection = start.add(u.scale(tValue));
			normal = intersection.subtract(pos);
		}

		return intersection;
	}

	public Vec3d fix(Vec3d vec) {
		Vec3d dist = vec.subtract(pos);
		if(dist.lengthVector() < radius) {
			return pos.add( dist.normalize().scale(radius+0.05) );
		}
		return vec;
	}

}