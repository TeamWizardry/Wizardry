package com.teamwizardry.wizardry.api;

import net.minecraft.util.math.Vec3d;

public class MutVec3d {

	public Vec3d vec3d;

	public MutVec3d(Vec3d vec3d) {
		this.vec3d = vec3d;
	}

	public MutVec3d(double x, double y, double z) {
		vec3d = new Vec3d(x, y, z);
	}

	public MutVec3d copy() {
		return new MutVec3d(vec3d.x, vec3d.y, vec3d.z);
	}
}
