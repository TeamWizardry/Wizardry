package com.teamwizardry.wizardry.client.particle

import com.teamwizardry.librarianlib.glitter.ParticleSystem
import com.teamwizardry.librarianlib.glitter.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.glitter.modules.DepthSortModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.glitter.modules.SpriteRenderOptions
import com.teamwizardry.wizardry.Wizardry
import com.teamwizardry.wizardry.common.PhysicsGlitterBox

class ParticlePhysicsGlitter : ParticleSystem(Wizardry.getID("physics_glitter")) {

    override fun configure() {
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)

        val gravity = bind(1)
        val bounciness = bind(1)
        val friction = bind(1)
        val damping = bind(1)

        val colorFrom = bind(4)
        val colorTo = bind(4)
        val colorEasing = bind(1)

        val alphaFrom = bind(1)
        val alphaTo = bind(1)
        val alphaEasing = bind(1)

        val sizeFrom = bind(1)
        val sizeTo = bind(1)
        val sizeEasing = bind(1)

        val depth = bind(1)

        globalUpdateModules.add(DepthSortModule(position, depth))

        updateModules.add(
            BasicPhysicsUpdateModule(
                position = position,
                previousPosition = previousPosition,
                velocity = velocity,
                enableCollision = false,
                gravity = gravity,
                bounciness = bounciness,
                friction = friction,
                damping = damping
            )
        )

        renderModules.add(
            SpriteRenderModule.build(
                SpriteRenderOptions.build(Wizardry.getID("textures/particles/sparkle_blurred.png")).additiveBlending().build(),
                position,
            )
                .previousPosition(previousPosition)
                .color(EaseAccessBinding(lifetime, age, null, null, colorEasing, 4, colorFrom, colorTo))
                .size(EaseAccessBinding(lifetime, age, null, null, sizeEasing, 1, sizeFrom, sizeTo))
                .alphaMultiplier(EaseAccessBinding(lifetime, age, null, null, alphaEasing, 1, alphaFrom, alphaTo))
                .build()
        )
    }

    fun spawn(
        box: PhysicsGlitterBox
    ) {
        this.addParticle(
            box.lifeSpan,

            // position
            box.origin.x,
            box.origin.y,
            box.origin.z,
            // previous position
            box.origin.x,
            box.origin.y,
            box.origin.z,

            // velocity
            box.velocity.x,
            box.velocity.y,
            box.velocity.z,

            // gravity
            box.gravity,
            // bounciness
            box.bounciness,
            // friction
            box.friction,
            // damping
            box.damping,

            // color
            box.startColor.red / 255.0,
            box.startColor.green / 255.0,
            box.startColor.blue / 255.0,
            box.startColor.alpha / 255.0,

            box.endColor.red / 255.0,
            box.endColor.green / 255.0,
            box.endColor.blue / 255.0,
            box.endColor.alpha / 255.0,

            ModParticles.easings.indexOf(box.colorEasing).toDouble(),

            // alpha
            box.startAlpha,
            box.endAlpha,
            ModParticles.easings.indexOf(box.alphaEasing).toDouble(),

            // size
            box.startSize,
            box.endSize,
            ModParticles.easings.indexOf(box.sizeEasing).toDouble(),

            // depth sorting
            1.0
        )
    }
}