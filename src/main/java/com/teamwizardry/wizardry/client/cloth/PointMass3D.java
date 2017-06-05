package com.teamwizardry.wizardry.client.cloth;

import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class PointMass3D {
	@Nullable
	public Vec3d pos, prevPos, origPos, friction;
	public float mass;
	public boolean pin;

	public PointMass3D(@Nullable Vec3d pos, float mass) {
		prevPos = this.pos = pos;
		this.mass = mass;
	}

	public void applyForce(Vec3d force) {
		pos = pos.add(force.scale(1.0 / mass));
	}

	public void applyMotion(Vec3d motion) {
		pos = pos.add(motion);
	}

	@Override
	public String toString() {
		return (pin ? "[P]" : "") + pos;
	}
}
