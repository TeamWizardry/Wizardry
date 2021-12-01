package com.teamwizardry.wizardry.client.particle

import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.ReadParticleBinding
import com.teamwizardry.librarianlib.glitter.bindings.ConstantBinding

abstract class AbstractTimeAccessBinding(
    /**
     * The lifetime binding for the particle. Generally [ParticleSystem.lifetime]
     */
    open val lifetime: ReadParticleBinding,
    /**
     * The age binding for the particle. Generally [ParticleSystem.age]
     */
    open val age: ReadParticleBinding,
    /**
     * The multiplier for the normalized age. If this value is > 1 the movement will loop, and if this value is < 1
     * the movement will end before the end of the path.
     */
    open val timescale: ReadParticleBinding? = ConstantBinding(1.0),
    /**
     * The time offset for the normalized age. Applied before the [timescale], so regardless of [timescale]'s value,
     * if the offset is 0.5, the animation will begin halfway along the path
     */
    open val offset: ReadParticleBinding? = ConstantBinding(0.0),
    /**
     * The easing to use if you want one to manipulate the binding.
     */
    open val easing: ReadParticleBinding? = null,
) : ReadParticleBinding {
    protected var time: Double = 0.0

    override fun load(particle: DoubleArray) {
        age.load(particle)
        lifetime.load(particle)
        var t = age.contents[0] / lifetime.contents[0]

        if (easing != null) {
            easing!!.load(particle)
            val easingIndex = easing!!.contents[0].toInt()
            val realEasing = ModParticles.easings[easingIndex]
            t = realEasing.ease(t.toFloat()).toDouble()
        }
        if (offset != null) t += offset!!.contents[0]
        if (timescale != null) t *= timescale!!.contents[0]
        if (t != 0.0) {
            t %= 1
            // because 1 % 1 = 0, but it should go up to 1 inclusive. If t is really 0 the above if won't pass
            if (t == 0.0) t = 1.0
        }
        time = t
    }
}

class EaseAccessBinding (
    /**
     * The lifetime binding for the particle. Generally [ParticleSystem.lifetime]
     */
    override val lifetime: ReadParticleBinding,
    /**
     * The age binding for the particle. Generally [ParticleSystem.age]
     */
    override val age: ReadParticleBinding,
    /**
     * The multiplier for the normalized age. If this value is > 1 the movement will loop, and if this value is < 1
     * the movement will end before the end of the path.
     */
    override val timescale: ReadParticleBinding? = null,
    /**
     * The time offset for the normalized age. Applied before the [timescale], so regardless of [timescale]'s value,
     * if the offset is 0.5, the animation will begin halfway along the path
     */
    override val offset: ReadParticleBinding? = null,
    /**
     * The easing to use when generating values for the binding.
     */
    override val easing: ReadParticleBinding? = null,
    /**
     * If working with a single number, set to 1, if a vector, set to 3 for x, y, z, if a color,
     * set to 4 for R, G, B, and A
     *
     * This exists to allow flexibility so you can ease whatever object you want no matter how many parameters it
     * may have.
     */
    val bindingSize: Int,
    /**
     * The start value to interpolate from.
     */
    val origin: ReadParticleBinding = ConstantBinding(*DoubleArray(bindingSize) { 0.0 }),
    /**
     * The end value to interpolate to.
     */
    var target: ReadParticleBinding = ConstantBinding(*DoubleArray(bindingSize) { 1.0 })
) : AbstractTimeAccessBinding(lifetime, age, timescale, offset, easing) {

    override val contents: DoubleArray = DoubleArray(bindingSize)

    init {
        lifetime.require(1)
        age.require(1)
        origin.require(bindingSize)
        target.require(bindingSize)
    }

    override fun load(particle: DoubleArray) {
        super.load(particle)
        origin.load(particle)
        target.load(particle)
        for (i in 0 until bindingSize) {
            contents[i] = (origin.contents[i] * (1 - time)) + (target.contents[i] * time)
        }
    }
}