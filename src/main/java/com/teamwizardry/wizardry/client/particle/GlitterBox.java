package com.teamwizardry.wizardry.client.particle;

import com.teamwizardry.wizardry.client.lib.LibTheme;
import net.minecraft.util.math.vector.Vector3d;

import java.awt.*;

public class GlitterBox {

	public final int lifetime;

	public final float originX, originY, originZ;
	public final float targetX, targetY, targetZ;
	public final boolean physics;

	public final Color initialColor;
	public final Color goalColor;

	public final float initialSize;
	public final float goalSize;

	public final float initialAlpha;
	public final float middleAlpha;
	public final float goalAlpha;

	public final float gravity;
	public final float drag;
	public final float friction;
	public final float bounce;

	public GlitterBox(int lifetime, float originX, float originY, float originZ, float targetX, float targetY,
					  float targetZ, boolean physics, Color initialColor, Color goalColor, float initialSize,
					  float goalSize, float initialAlpha, float middleAlpha, float goalAlpha, float gravity, float drag,
					  float friction, float bounce) {
		this.lifetime = lifetime;
		this.originX = originX;
		this.originY = originY;
		this.originZ = originZ;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.physics = physics;
		this.initialColor = initialColor;
		this.goalColor = goalColor;
		this.initialSize = initialSize;
		this.goalSize = goalSize;
		this.initialAlpha = initialAlpha;
		this.middleAlpha = middleAlpha;
		this.goalAlpha = goalAlpha;
		this.gravity = gravity;
		this.drag = drag;
		this.friction = friction;
		this.bounce = bounce;
	}

	public static class GlitterBoxFactory {
		private float originX = 0, originY = 0, originZ = 0;
		private float targetX = 0, targetY = 0, targetZ = 0;
		private boolean physics = true;
		private Color initialColor = LibTheme.accentColor;
		private Color goalColor = LibTheme.accentColor;
		private float initialSize = 30;
		private float goalSize = -1;
		private float gravity = 0;
		private float drag = 0;
		private float friction = 0;
		private float bounce = 0;
		private float initialAlpha = 1;
		private float middleAlpha = 1;
		private float goalAlpha = 1;

		public GlitterBoxFactory setGoalAlpha(float goalAlpha) {
			this.goalAlpha = goalAlpha;
			return this;
		}

		public GlitterBoxFactory setMiddleAlpha(float middleAlpha) {
			this.middleAlpha = middleAlpha;
			return this;
		}

		public GlitterBoxFactory setInitialAlpha(float initialAlpha) {
			this.initialAlpha = initialAlpha;
			return this;

		}

		public GlitterBoxFactory setOrigin(Vector3d origin) {
			return setOrigin(origin.x, origin.y, origin.z);
		}

		public GlitterBoxFactory setOrigin(float x, float y, float z) {
			this.originX = x;
			this.originY = y;
			this.originZ = z;
			return this;
		}

		public GlitterBoxFactory setOrigin(double x, double y, double z) {
			return setOrigin((float) x, (float) y, (float) z);

		}

		public GlitterBoxFactory setTarget(Vector3d target) {
			return setTarget(target.x, target.y, target.z);
		}

		public GlitterBoxFactory setTarget(float x, float y, float z) {
			this.targetX = x;
			this.targetY = y;
			this.targetZ = z;
			return this;
		}

		public GlitterBoxFactory setTarget(double x, double y, double z) {
			return setTarget((float) x, (float) y, (float) z);
		}

		public GlitterBoxFactory setIsPhysics(boolean isPhysics) {
			this.physics = isPhysics;
			return this;
		}

		public GlitterBoxFactory setInitialColor(Color initialColor) {
			this.initialColor = initialColor;
			return this;
		}

		public GlitterBoxFactory setGoalColor(Color goalColor) {
			this.goalColor = goalColor;
			return this;
		}

		public GlitterBoxFactory setInitialSize(float initialSize) {
			this.initialSize = initialSize;
			return this;
		}

		public GlitterBoxFactory setGoalSize(float goalSize) {
			this.goalSize = goalSize;
			return this;
		}

		public GlitterBoxFactory setGravity(float gravity) {
			this.gravity = gravity;
			return this;
		}

		public GlitterBoxFactory setDrag(float drag) {
			this.drag = drag;
			return this;
		}

		public GlitterBoxFactory setFriction(float friction) {
			this.friction = friction;
			return this;
		}

		public GlitterBoxFactory setBounce(float bounce) {
			this.bounce = bounce;
			return this;
		}

		public GlitterBox createGlitterBox(int lifetime) {
			return new GlitterBox(lifetime,
					originX, originY, originZ,
					targetX, targetY, targetZ,
					physics, initialColor, goalColor == null ? initialColor : goalColor,
					initialSize, goalSize == -1 ? initialSize : goalSize,
					initialAlpha, middleAlpha, goalAlpha, gravity,
					drag,
					friction,
					bounce);
		}
	}
}
