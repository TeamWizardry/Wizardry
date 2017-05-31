package com.teamwizardry.wizardry.api;

import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by LordSaad.
 */
public class LightningGenerator {

	private Vec3d point1;
	private Vec3d point2;

	public LightningGenerator(Vec3d point1, Vec3d point2) {
		this.point1 = point1;
		this.point2 = point2;
	}

	public ArrayList<Vec3d> generate() {
		ArrayList<Vec3d> results = new ArrayList<>();
		Vec3d sub = point2.subtract(point1);
		Vec3d normal = sub.normalize();
		double dist = sub.lengthVector();

		ArrayList<Float> points = new ArrayList<>();
		points.add(0f);
		for (int i = 0; i < dist * 4; i++) {
			points.add(RandUtil.nextFloat());
		}

		Collections.sort(points);

		Vec3d prevPoint = point1;
		double prevScale = 0;
		for (int i = 1; i < points.size(); i++) {

			float point = points.get(i);
			Vec3d vec = sub.scale(point);

			double scale = dist * 0.02 + (point - (points.get(i - 1)));
			prevScale = scale;

			vec = new Vec3d(
					vec.xCoord + RandUtil.nextDouble(-scale, scale),
					vec.yCoord + RandUtil.nextDouble(-scale, scale),
					vec.zCoord + RandUtil.nextDouble(-scale, scale)
			);

			if (RandUtil.nextInt(10) == 0) {
				float[] pitchyaw = PosUtils.vecToRotations(normal);
				float pitch = pitchyaw[0];
				float yaw = pitchyaw[1];
				double angle = 270;
				float newPitch = (float) (pitch + RandUtil.nextDouble(-angle, angle));
				float newYaw = (float) (yaw + RandUtil.nextDouble(-angle, angle));
				Vec3d offset = PosUtils.vecFromRotations(newPitch, newYaw).scale(dist / 3);

				results.addAll(new LightningGenerator(point1.add(vec), point1.add(vec).add(offset)).generate());
			}

			results.add(point1.add(vec));
			prevPoint = vec;
		}

		results.add(point2);
		return results;
	}
}
