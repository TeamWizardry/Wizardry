package com.teamwizardry.wizardry.common

import com.teamwizardry.librarianlib.math.Easing
import net.minecraft.util.math.Vec3d
import java.awt.Color

open class AbstractGlitterBox

class PhysicsGlitterBox(
    val lifeSpan: Int,
    val origin: Vec3d,
    val startColor: Color,
    val endColor: Color = startColor,
    val colorEasing: Easing = Easing.linear,
    val startAlpha: Double,
    val endAlpha: Double = startAlpha,
    val alphaEasing: Easing = Easing.linear,
    val velocity: Vec3d = Vec3d.ZERO,
    val startSize: Double = 0.5,
    val endSize: Double = startSize,
    val sizeEasing: Easing = Easing.linear,
    val gravity: Double = 0.04,
    val bounciness: Double = 0.2,
    val friction: Double = 0.2,
    val damping: Double = 0.01
) : AbstractGlitterBox() {

    private constructor(builder: Builder) : this(
        builder.lifeSpan,
        builder.origin,
        builder.startColor,
        builder.endColor,
        builder.colorEasing,
        builder.startAlpha,
        builder.endAlpha,
        builder.alphaEasing,
        builder.velocity,
        builder.startSize,
        builder.endSize,
        builder.sizeEasing,
        builder.gravity,
        builder.bounciness,
        builder.friction,
        builder.damping,
    )

    companion object {
        inline fun build(
            lifeSpan: Int,
            origin: Vec3d,
            color: Color, block: Builder.() -> Unit
        ) = Builder(lifeSpan, origin, color).apply(block).build()
    }

    class Builder(
        val lifeSpan: Int,
        val origin: Vec3d,
        val startColor: Color,
    ) {
        var velocity: Vec3d = Vec3d.ZERO
        var startSize: Double = 0.5
        var gravity: Double = 0.04
        var bounciness: Double = 0.2
        var friction: Double = 0.2
        var damping: Double = 0.01

        var startAlpha: Double = 1.0
        var endAlpha = startAlpha
        var alphaEasing: Easing = Easing.linear
        var endColor = startColor
        var colorEasing: Easing = Easing.linear
        var endSize = startSize
        var sizeEasing: Easing = Easing.linear

        fun build() = PhysicsGlitterBox(this)
    }
}