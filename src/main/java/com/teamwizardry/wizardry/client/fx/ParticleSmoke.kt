package com.teamwizardry.wizardry.client.fx

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.EaseBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SetValueUpdateModule
import com.teamwizardry.librarianlib.features.particlesystem.modules.SpriteRenderModule
import com.teamwizardry.wizardry.Wizardry
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d

class ParticleSmoke : ParticleSystem() {

    override fun configure() {
        val size = bind(1)
        val pos = bind(3)
        val prevPos = bind(3)
        val velocity = bind(3)
        val alpha = bind(1)

        updateModules.add(BasicPhysicsUpdateModule(
                pos,
                prevPos,
                velocity = velocity,
                gravity = -0.001,
                enableCollision = false,
                damping = 0.1f,
                friction = 0.1f
        ))

        updateModules.add(SetValueUpdateModule(
                alpha, EaseBinding(lifetime = lifetime, age = age, bindingSize = 1, easing = Easing.easeInOutLinear(0.5f, 1f, 0.5f))
        ))

        renderModules.add(SpriteRenderModule(
                sprite = ResourceLocation(Wizardry.MODID, "textures/particles/smoke_2.png"),
                enableBlend = true,
                previousPosition = prevPos,
                position = pos,
                alphaMultiplier = alpha,
                size = size))
    }

    fun spawn(lifetime: Double, size: Double, pos: Vec3d, velocity: Vec3d) {
        this.addParticle(lifetime,
                size,
                pos.x, pos.y, pos.z,
                pos.x, pos.y, pos.z,
                velocity.x, velocity.y, velocity.z,
                0.5
        )
    }
}
