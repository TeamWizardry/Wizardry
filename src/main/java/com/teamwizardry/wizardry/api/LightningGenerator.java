package com.teamwizardry.wizardry.api;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

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

	private ArrayList<Vec3d> offsetRandomPoints() {
		ArrayList<Vec3d> results = new ArrayList<>();
		Vec3d tangent = point2.subtract(point1);
		Vec3d normal = tangent.normalize();
		float length = (float) tangent.lengthVector();

		ArrayList<Float> positions = new ArrayList<>();
		positions.add(0f);

		for (int i = 0; i < length / 4; i++)
			positions.add(ThreadLocalRandom.current().nextFloat());

		float Sway = 80;
		float Jaggedness = 1 / Sway;

		Vec3d prevPoint = point1;
		float prevDisplacement = 0;
		for (int i = 1; i < positions.size() - 1; i++) {
			float pos = positions.get(i);

			// used to prevent sharp angles by ensuring very close positions also have small perpendicular variation.
			float scale = (length * Jaggedness) * (pos - positions.get(i - 1));

			// defines an envelope. Points near the middle of the bolt can be further from the central line.
			float envelope = pos > 0.95f ? 20 * (1 - pos) : 1;

			float displacement = (float) ThreadLocalRandom.current().nextDouble(-Sway, Sway);
			displacement -= (displacement - prevDisplacement) * (1 - scale);
			displacement *= envelope;

			Vec3d point = point1.add(tangent.scale(pos).add(normal.scale(displacement)));
			//results.add(new Line(prevPoint, point, 8f));
			prevPoint = point;
			prevDisplacement = displacement;
		}

		//results.add(new Line(prevPoint, point2, 8f));

		return results;
	}
}
