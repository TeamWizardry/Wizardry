package com.teamwizardry.wizardry.common.utils

object MathUtils {
    fun genRandomDotInCircle(radius: Float): Vec2d {
        val theta = 2.0f * Math.PI.toFloat() * RandUtil.nextFloat()
        val r = radius * RandUtil.nextFloat()
        val x: Float = r * MathHelper.cos(theta)
        val y: Float = r * MathHelper.sin(theta)
        return Vec2d(x, y)
    }

    fun genCirclePerimeterDot(radius: Float, angle: Float): Vec2d {
        val theta = 2.0f * Math.PI.toFloat() * angle
        val x: Float = radius * MathHelper.cos(theta)
        val y: Float = radius * MathHelper.sin(theta)
        return Vec2d(x, y)
    }
}