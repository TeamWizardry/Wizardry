package com.teamwizardry.wizardry.api;

import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by LordSaad.
 */
public class LightningGenerator {

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
		for (int i = 0; i < dist * 6; i++) {
			points.add(rand.nextFloat());
		}

		Collections.sort(points);

		for (int i = 1; i < points.size(); i++) {

			float point = points.get(i);
			Vec3d vec = sub.scale(point);

			double scale = dist * 0.005 + (point - (points.get(i - 1)));

			vec = new Vec3d(
					vec.xCoord + rand.nextDouble(-scale, scale),
					vec.yCoord + rand.nextDouble(-scale, scale),
					vec.zCoord + rand.nextDouble(-scale, scale)
			);

			if (rand.nextInt(10) == 0) {
				float[] pitchyaw = PosUtils.vecToRotations(normal);
				float pitch = pitchyaw[0];
				float yaw = pitchyaw[1];
				double angle = 340;
				float newPitch = (float) (pitch + rand.nextDouble(-angle, angle));
				float newYaw = (float) (yaw + rand.nextDouble(-angle, angle));
				Vec3d offset = PosUtils.vecFromRotations(newPitch, newYaw).scale(dist / 3.5f);

				results.addAll(new LightningGenerator(point1.add(vec), point1.add(vec).add(offset), rand).generate());
			}

			results.add(point1.add(vec));
		}

		results.add(point2);
		return results;
	}
}
