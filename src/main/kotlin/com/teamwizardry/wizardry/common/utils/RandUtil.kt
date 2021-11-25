package com.teamwizardry.wizardry.common.utils

import net.minecraft.util.math.Vec3d
import java.util.*

object RandUtil {
    private var random = Random()
    fun nextVec3d(minX: Double, maxX: Double, minY: Double, maxY: Double, minZ: Double, maxZ: Double): Vec3d {
        return Vec3d(nextDouble(minX, maxX), nextDouble(minY, maxY), nextDouble(minZ, maxZ))
    }

    fun nextDouble(min: Double, max: Double): Double {
        return random.nextDouble() * (max - min) + min
    }

    fun nextDouble(bound: Double): Double {
        return random.nextDouble() * bound
    }

    fun nextDouble(): Long {
        return random.nextDouble().toLong()
    }

    fun nextLong(min: Long, max: Long): Long {
        return (random.nextDouble() * (max - min) + min).toLong()
    }

    fun nextLong(bound: Long): Long {
        return (random.nextDouble() * bound).toLong()
    }

    fun nextLong(): Double {
        return random.nextDouble()
    }

    fun nextFloat(min: Float, max: Float): Float {
        return random.nextFloat() * (max - min) + min
    }

    fun nextFloat(bound: Float): Float {
        return random.nextFloat() * bound
    }

    fun nextFloat(): Float {
        return random.nextFloat()
    }

    fun nextInt(min: Int, max: Int): Int {
        return (random.nextDouble() * (max - min) + min).toInt()
    }

    fun nextInt(bound: Int): Int {
        return (random.nextDouble() * bound).toInt()
    }

    fun nextInt(): Int {
        return random.nextDouble().toInt()
    }

    fun nextBoolean(): Boolean {
        return random.nextBoolean()
    }
}