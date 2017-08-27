package com.teamwizardry.wizardry.client.core;

import com.google.common.primitives.Doubles;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

abstract class Vertex {

	Vec3d lastPos;
	public Vec3d pos;
	boolean pinned;
	Vec3d scratch = Vec3d.ZERO;

	Vertex(Vec3d pos, boolean pinned) {
		this.lastPos = this.pos = pos;
		this.pinned = pinned;
	}

	Vertex(Vec3d pos) {
		this.lastPos = this.pos = pos;
		this.pinned = false;
	}

	abstract Vec3d normal();
}

class Constraint {

	public final Vertex a;
	public final Vertex b;
	public final double length;
	private final boolean hard;

	Constraint(Vertex a, Vertex b, double length, boolean hard) {
		this.a = a;
		this.b = b;
		this.length = length;
		this.hard = hard;
	}

	Constraint(Vertex a, Vertex b, boolean hard) {
		this.a = a;
		this.b = b;
		this.length = a.pos.subtract(b.pos).lengthVector();
		this.hard = hard;
	}

	Constraint(Vertex a, Vertex b) {
		this.a = a;
		this.b = b;
		this.length = a.pos.subtract(b.pos).lengthVector();
		this.hard = false;
	}

	void resolve(double coeff) {
		if (a.pinned && b.pinned) return;
		Vec3d delta = b.pos.subtract(a.pos);
		double distance = delta.lengthVector();

		if (distance == 0.0) return;

		Vec3d normalBToA = delta.scale(1 / distance);
		double deltaDistance = this.length - distance;

		if (deltaDistance == 0.0) return;
		if (!Doubles.isFinite(distance)) return;

		if (a.pinned || hard) {
			b.pos = b.pos.add(normalBToA.scale(deltaDistance * coeff));
		} else if (b.pinned) {
			a.pos = a.pos.add(normalBToA.scale(-1).scale(deltaDistance * coeff));
		} else {
			b.pos = b.pos.add(normalBToA.scale((deltaDistance / 2) * coeff));
			a.pos = a.pos.add(normalBToA.scale(-1).scale((deltaDistance / 2) * coeff));
		}
	}
}

public class Cape {
	private double inertialDampingCoeff = 1.0;
	double dragDampingCoeff = 0.8;
	private double springCoeff = 0.8;
	Vec3d windVelocity = Vec3d.ZERO;
	double windForce = 0.1;
	Vec3d gravity = Vec3d.ZERO;
	List<Vertex> points = new ArrayList<>();
	private List<Constraint> constraints = new ArrayList<>();

	public Cape(ClothDefinition def) {
		def.generate(points, constraints);
	}

	public void tick() {
		for (Vertex vertex : points) {
			vertex.lastPos = vertex.scratch;
		}

		assembleBBs();
		shiftForAirForce();
		shiftForInertia();
		shiftForAcceleration();
		applyDampening();
		for (int i = 0; i < 3; i++) {
			resolve();
		}

		for (Vertex vertex : points) {
			vertex.scratch = vertex.pos;
		}
	}

	private void assembleBBs() {
	}

	private void shiftForInertia() {
		for (Vertex vertex : points) {
			if (vertex.pinned) continue;
			vertex.pos = vertex.pos.add(vertex.pos.subtract(vertex.lastPos).scale(inertialDampingCoeff));
		}
	}

	private void shiftForAcceleration() {
		for (Vertex vertex : points) {
			if (vertex.pinned) continue;
			vertex.pos = vertex.pos.add(gravity);

		}
	}

	private void shiftForAirForce() {
		for (Vertex vertex : points) {
			if (vertex.pinned) continue;

			Vec3d vel = vertex.pos.subtract(vertex.lastPos);
			Vec3d airVel = vel.add(windVelocity);
			Vec3d norm = vertex.normal();
			Vec3d proj = norm.scale(norm.dotProduct(airVel));

			vertex.scratch = proj.scale(windForce);
		}

		for (Vertex vertex : points) {
			if (!vertex.pinned) vertex.pos = vertex.pos.add(vertex.scratch);
		}
	}

	private void applyDampening() {
		for (Vertex vertex : points) {
			vertex.pos = vertex.lastPos.add(vertex.pos.subtract(vertex.lastPos).scale(dragDampingCoeff));
		}
	}

	private void resolve() {
		for (Constraint constraint : constraints) {
			constraint.resolve(springCoeff);
		}
	}
}

interface ClothDefinition {
	void generate(List<Vertex> points, List<Constraint> constraints);
}

class GridCloth implements ClothDefinition {

	public final Vec3d origin;
	private final Vec3d widthUnit;
	private final Vec3d heightUnit;
	public final int width;
	public final int height;

	GridCloth(Vec3d origin, Vec3d widthUnit, Vec3d heightUnit, int width, int height) {
		this.origin = origin;
		this.widthUnit = widthUnit;
		this.heightUnit = heightUnit;
		this.width = width;
		this.height = height;
	}

	@Override
	public void generate(List<Vertex> points, List<Constraint> constraints) {
		List<Vertex> newPoints = new ArrayList<>();
		List<Constraint> newConstraints = new ArrayList<>();
		addPoints(newPoints);

		for (int i = 0; i < newPoints.size() - 1; i++) {
			addPositiveNeighbors(newConstraints, newPoints, i);
		}

		points.addAll(newPoints);
		constraints.addAll(newConstraints);
	}

	private void addPoints(List<Vertex> list) {

		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {

				int finalH = h;
				int finalW = w;
				Vertex vertex = new Vertex(origin.add(widthUnit.scale(finalW)).add(heightUnit.scale(finalH))) {
					@Override
					Vec3d normal() {
						Vec3d c = this.pos;
						Vec3d u;
						if (valid(finalW, finalH - 1)) u = list.get(getI(finalW, finalH - 1)).pos;
						else u = null;
						Vec3d d;
						if (valid(finalW, finalH + 1)) d = list.get(getI(finalW, finalH + 1)).pos;
						else d = null;
						Vec3d l;
						if (valid(finalW - 1, finalH)) l = list.get(getI(finalW - 1, finalH)).pos;
						else l = null;
						Vec3d r;
						if (valid(finalW + 1, finalH)) r = list.get(getI(finalW + 1, finalH)).pos;
						else r = null;

						Vec3d avg = Vec3d.ZERO;

						if (u != null && l != null) {
							avg.add(c.subtract(u).crossProduct(c.subtract(l)));
						}
						if (u != null && r != null) {
							avg.add(c.subtract(r).crossProduct(c.subtract(u)));
						}
						if (d != null && l != null) {
							avg.add(c.subtract(l).crossProduct(c.subtract(d)));
						}
						if (d != null && r != null) {
							avg.add((c.subtract(d)).crossProduct(c.subtract(r)));
						}

						if (avg.x == 0.0 && avg.y == 0.0 && avg.z == 0.0)
							return avg;
						return avg.normalize();
					}
				};

				list.add(vertex);
			}
		}
	}

	private void addPositiveNeighbors(List<Constraint> constraints, List<Vertex> list, int index) {
		Vertex v = list.get(index);
		int w = getW(index);
		int h = getH(index);

		if (valid(w, h + 1))
			constraints.add(new Constraint(v, list.get(getI(w, h + 1)), true)); // down
		if (valid(w + 1, h))
			constraints.add(new Constraint(v, list.get(getI(w + 1, h)))); // right
		if (valid(w + 1, h + 1))
			constraints.add(new Constraint(v, list.get(getI(w + 1, h + 1)))); // down-right
		if (valid(w - 1, h + 1))
			constraints.add(new Constraint(v, list.get(getI(w - 1, h + 1)))); // down-left
	}

	private int getW(int index) {
		return index % (width + 1);
	}

	private int getH(int index) {
		return Math.floorDiv(index, width + 1);
	}

	private int getI(int w, int h) {
		return w + width * h;
	}

	private boolean valid(int w, int h) {
		return (w >= 0 && h >= 0) && (w < width && h < height);
	}
}

class LineCloth implements ClothDefinition {

	public final Vec3d origin;
	private final Vec3d unit;
	public final int length;

	LineCloth(Vec3d origin, Vec3d unit, int length) {

		this.origin = origin;
		this.unit = unit;
		this.length = length;
	}

	@Override
	public void generate(List<Vertex> points, List<Constraint> constraints) {
		ArrayList<Vertex> newPoints = new ArrayList<>();
		ArrayList<Constraint> newConstraints = new ArrayList<>();
		addPoints(newPoints);

		for (int i = 0; i < length - 1; i++) {
			newConstraints.add(new Constraint(newPoints.get(i), newPoints.get(i + 1), true));
		}

		points.addAll(newPoints);
		constraints.addAll(newConstraints);
	}

	private void addPoints(ArrayList<Vertex> list) {
		for (int i = 0; i < length; i++) {
			Vertex vertex = new Vertex(origin.add(unit.scale(i))) {
				@Override
				Vec3d normal() {
					return Vec3d.ZERO;
				}
			};
			list.add(vertex);
		}
	}
}