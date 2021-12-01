package com.teamwizardry.wizardry.common.utils

import com.teamwizardry.librarianlib.math.Vec2d
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

object MathUtils {

    /**
     * https://stackoverflow.com/a/15048260
     */
    fun genRandomPointInSphere(radius: Double): Vec3d {
        val u = Math.random()
        val v = Math.random()
        val theta = 2 * Math.PI * u
        val phi = acos(2 * v - 1)
        val x = (radius * sin(phi) * cos(theta))
        val y = (radius * sin(phi) * sin(theta))
        val z = (radius * cos(phi))

        return Vec3d(x, y, z)
    }

    fun genCirclePerimeterDot(radius: Float, angle: Float): Vec2d {
        val theta = 2.0f * Math.PI.toFloat() * angle
        val x: Float = radius * MathHelper.cos(theta)
        val y: Float = radius * MathHelper.sin(theta)
        return Vec2d(x.toDouble(), y.toDouble())
    }
}