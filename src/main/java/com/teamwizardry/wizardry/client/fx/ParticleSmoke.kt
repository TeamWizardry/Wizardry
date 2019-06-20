package com.teamwizardry.wizardry.client.fx

import com.teamwizardry.librarianlib.features.animator.Easing
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut
import com.teamwizardry.librarianlib.features.particlesystem.ParticleSystem
import com.teamwizardry.librarianlib.features.particlesystem.bindings.InterpBinding
import com.teamwizardry.librarianlib.features.particlesystem.modules.BasicPhysicsUpdateModule
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

        renderModules.add(SpriteRenderModule(
                sprite = ResourceLocation(Wizardry.MODID, "textures/particles/smoke_2.png"),
                enableBlend = true,
                previousPosition = prevPos,
                position = pos,
                alphaMultiplier = InterpBinding(lifetime, age, interp = InterpFloatInOut(0, 1, 0), easing = Easing.easeOutQuart),
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
