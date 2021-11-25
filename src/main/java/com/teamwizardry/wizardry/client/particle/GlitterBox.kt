package com.teamwizardry.wizardry.client.particle

import com.teamwizardry.wizardry.client.lib.LibTheme
import net.minecraft.util.math.Vec3d
import java.awt.Color

class GlitterBox(
    val lifetime: Int,
    val originX: Float,
    val originY: Float,
    val originZ: Float,
    val targetX: Float,
    val targetY: Float,
    val targetZ: Float,
    val physics: Boolean,
    val initialColor: Color,
    val goalColor: Color,
    val initialSize: Float,
    val goalSize: Float,
    val initialAlpha: Float,
    val middleAlpha: Float,
    val goalAlpha: Float,
    val gravity: Float,
    val drag: Float,
    val friction: Float,
    val bounce: Float
) {
    class GlitterBoxFactory {
        private var originX = 0f
        private var originY = 0f
        private var originZ = 0f
        private var targetX = 0f
        private var targetY = 0f
        private var targetZ = 0f
        private var physics = true
        private var initialColor: Color = LibTheme.accentColor
        private var goalColor: Color? = LibTheme.accentColor
        private var initialSize = 30f
        private var goalSize = -1f
        private var gravity = 0f
        private var drag = 0f
        private var friction = 0f
        private var bounce = 0f
        private var initialAlpha = 1f
        private var middleAlpha = 1f
        private var goalAlpha = 1f
        fun setGoalAlpha(goalAlpha: Float): GlitterBoxFactory {
            this.goalAlpha = goalAlpha
            return this
        }

        fun setMiddleAlpha(middleAlpha: Float): GlitterBoxFactory {
            this.middleAlpha = middleAlpha
            return this
        }

        fun setInitialAlpha(initialAlpha: Float): GlitterBoxFactory {
            this.initialAlpha = initialAlpha
            return this
        }

        fun setOrigin(origin: Vec3d): GlitterBoxFactory {
            return setOrigin(origin.x, origin.y, origin.z)
        }

        fun setOrigin(x: Float, y: Float, z: Float): GlitterBoxFactory {
            originX = x
            originY = y
            originZ = z
            return this
        }

        fun setOrigin(x: Double, y: Double, z: Double): GlitterBoxFactory {
            return setOrigin(x.toFloat(), y.toFloat(), z.toFloat())
        }

        fun setTarget(target: Vec3d): GlitterBoxFactory {
            return setTarget(target.x, target.y, target.z)
        }

        fun setTarget(x: Float, y: Float, z: Float): GlitterBoxFactory {
            targetX = x
            targetY = y
            targetZ = z
            return this
        }

        fun setTarget(x: Double, y: Double, z: Double): GlitterBoxFactory {
            return setTarget(x.toFloat(), y.toFloat(), z.toFloat())
        }

        fun setIsPhysics(isPhysics: Boolean): GlitterBoxFactory {
            physics = isPhysics
            return this
        }

        fun setInitialColor(initialColor: Color): GlitterBoxFactory {
            this.initialColor = initialColor
            return this
        }

        fun setGoalColor(goalColor: Color?): GlitterBoxFactory {
            this.goalColor = goalColor
            return this
        }

        fun setInitialSize(initialSize: Float): GlitterBoxFactory {
            this.initialSize = initialSize
            return this
        }

        fun setGoalSize(goalSize: Float): GlitterBoxFactory {
            this.goalSize = goalSize
            return this
        }

        fun setGravity(gravity: Float): GlitterBoxFactory {
            this.gravity = gravity
            return this
        }

        fun setDrag(drag: Float): GlitterBoxFactory {
            this.drag = drag
            return this
        }

        fun setFriction(friction: Float): GlitterBoxFactory {
            this.friction = friction
            return this
        }

        fun setBounce(bounce: Float): GlitterBoxFactory {
            this.bounce = bounce
            return this
        }

        fun createGlitterBox(lifetime: Int): GlitterBox {
            return GlitterBox(
                lifetime,
                originX, originY, originZ,
                targetX, targetY, targetZ,
                physics, initialColor, (if (goalColor == null) initialColor else goalColor)!!,
                initialSize, if (goalSize == -1f) initialSize else goalSize,
                initialAlpha, middleAlpha, goalAlpha, gravity,
                drag,
                friction,
                bounce
            )
        }
    }
}