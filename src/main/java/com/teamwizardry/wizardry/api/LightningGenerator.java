package com.teamwizardry.wizardry.api;

import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Demoniaque.
 */
public class LightningGenerator {

	public static final int POINTS_PER_DIST = 6;

	private Vec3d point1;
	private Vec3d point2;
	private RandUtilSeed rand;

	public LightningGenerator(Vec3d point1, Vec3d point2, RandUtilSeed rand) {
		this.point1 = point1;
		this.point2 = point2;
		this.rand = rand;
	}

	public ArrayList<Vec3d> generate() {
		ArrayList<Vec3d> results = new ArrayList<>();
		Vec3d sub = point2.subtract(point1);
		Vec3d normal = sub.normalize();
		double dist = sub.lengthVector();

		ArrayList<Float> points = new ArrayList<>();
		points.add(0f);
		for (int i = 0; i < dist * POINTS_PER_DIST; i++) {
			points.add(rand.nextFloat());
		}

		Collections.sort(points);

		for (int i = 1; i < points.size(); i++) {

			float point = points.get(i);
			Vec3d vec = sub.scale(point);

			double scale = dist * 0.01 + (point - (points.get(i - 1)));

			vec = new Vec3d(
					vec.x + rand.nextDouble(-scale, scale),
					vec.y + rand.nextDouble(-scale, scale),
					vec.z + rand.nextDouble(-scale, scale)
			);

			if (rand.nextInt(10) == 0) {
				float angle = 20;
				Vec3d offset = normal.rotateYaw(rand.nextFloat(-angle, angle)).rotatePitch(rand.nextFloat(-angle, angle));

				results.addAll(new LightningGenerator(point1.add(vec), point1.add(vec).add(offset), rand).generate());
			}

			results.add(point1.add(vec));
		}

		results.add(point2);
		return results;
	}
}
