package com.teamwizardry.wizardry.client.particle;

import com.teamwizardry.librarianlib.math.Easing;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class GlitterBox {

	public final int lifetime;

	public final Vec3d origin;
	public final Vec3d target;
	public final Easing translationEasing;

	public final Color initialColor;
	public final Color goalColor;
	public final Easing colorEasing;

	public final float initialSize;
	public final float goalSize;
	public final Easing sizeEasing;

	public final float gravity;
	public final float drag;
	public final float friction;
	public final float bounce;

	private GlitterBox(int lifetime, Vec3d origin, Vec3d target, Easing translationEasing, Color initialColor, Color goalColor, Easing colorEasing, float initialSize, float goalSize, Easing sizeEasing, float gravity, float drag, float friction, float bounce) {
		this.lifetime = lifetime;
		this.origin = origin;
		this.target = target;
		this.translationEasing = translationEasing;
		this.initialColor = initialColor;
		this.goalColor = goalColor;
		this.colorEasing = colorEasing;
		this.initialSize = initialSize;
		this.goalSize = goalSize;
		this.sizeEasing = sizeEasing;
		this.gravity = gravity;
		this.drag = drag;
		this.friction = friction;
		this.bounce = bounce;
	}

	public static class GlitterBoxFactory {
		private Vec3d origin = Vec3d.ZERO;
		private Vec3d target = Vec3d.ZERO;
		private Easing translationEasing = Easing.easeOutQuart;
		private Color initialColor = Color.CYAN;
		private Color goalColor = Color.CYAN;
		private Easing colorEasing = Easing.linear;
		private float initialSize = 30;
		private float goalSize = -1;
		private Easing sizeEasing = Easing.easeOutQuart;
		private float gravity = 0;
		private float drag = 0;
		private float friction = 0;
		private float bounce = 0;

		public GlitterBoxFactory setOrigin(Vec3d origin) {
			this.origin = origin;
			return this;
		}

		public GlitterBoxFactory setTarget(Vec3d target) {
			this.target = target;
			return this;
		}

		public GlitterBoxFactory setTranslationEasing(Easing translationEasing) {
			this.translationEasing = translationEasing;
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

		public GlitterBoxFactory setColorEasing(Easing colorEasing) {
			this.colorEasing = colorEasing;
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

		public GlitterBoxFactory setSizeEasing(Easing sizeEasing) {
			this.sizeEasing = sizeEasing;
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
					origin, target == null ? origin : target, translationEasing,
					initialColor, goalColor == null ? initialColor : goalColor, colorEasing,
					initialSize, goalSize == -1 ? initialSize : goalSize, sizeEasing,
					gravity,
					drag,
					friction,
					bounce);
		}
	}
}
