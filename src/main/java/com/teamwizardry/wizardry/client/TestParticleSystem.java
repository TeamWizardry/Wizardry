package com.teamwizardry.wizardry.client;

import com.teamwizardry.librarianlib.particles.ParticleBinding;
import com.teamwizardry.librarianlib.particles.ParticleSystem;
import com.teamwizardry.librarianlib.particles.bindings.ConstantBinding;
import com.teamwizardry.librarianlib.particles.bindings.StoredBinding;
import com.teamwizardry.librarianlib.particles.modules.BasicPhysicsUpdateModule;
import com.teamwizardry.librarianlib.particles.modules.SpriteRenderModule;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class TestParticleSystem extends ParticleSystem {
    public static final TestParticleSystem INSTANCE = new TestParticleSystem();
    private TestParticleSystem() {}

    @Override
    public void configure() {
        StoredBinding
                position = bind(3),
                previousPosition = bind(3),
                velocity = bind(3),
                color = bind(4);

        getUpdateModules().add(new BasicPhysicsUpdateModule(
                position, previousPosition, velocity,
                true, 0.02, 0.8f, 0.02f, 0.01f
        ));
        getRenderModules().add(new SpriteRenderModule(
                SpriteRenderModule.simpleRenderType(new ResourceLocation("wizardry:textures/particles/sparkle.png")),
                position, previousPosition, color, new ConstantBinding(0.25)
        ));
    }

    public void spawn(Entity player) {
        Vec3d eyePos = player.getEyePosition(0);
        Vec3d look = player.getLookVec();

        double spawnDistance = 2, spawnVelocity = 0.2;

        addParticle(200,
                eyePos.x + look.x * spawnDistance,
                eyePos.y + look.y * spawnDistance,
                eyePos.z + look.z * spawnDistance,
                eyePos.x + look.x * spawnDistance,
                eyePos.y + look.y * spawnDistance,
                eyePos.z + look.z * spawnDistance,
                look.x * spawnVelocity,
                look.y * spawnVelocity,
                look.z * spawnVelocity,
                Math.random(),
                Math.random(),
                Math.random(),
                1.0
        );
    }
}
