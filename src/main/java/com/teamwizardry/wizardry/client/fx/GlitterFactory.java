package com.teamwizardry.wizardry.client.fx;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by saad4 on 24/7/2016.
 */
public class GlitterFactory {

    private static GlitterFactory INSTANCE = new GlitterFactory();

    private GlitterFactory() {
    }

    public static GlitterFactory getInstance() {
        return INSTANCE;
    }

    public SparkleFX createSparkle(World world, Vec3d origin, int age) {
        SparkleFX fx = Wizardry.proxy.spawnParticleSparkle(world, origin);
        if (ThreadLocalRandom.current().nextInt(Wizardry.proxy.getParticleDensity() / 100) <= 1) {
            fx.setMaxAge(age * Wizardry.proxy.getParticleDensity() / 100);
        } else fx.setMaxAge(0);
        return fx;
    }

    public SparkleFX createSparkle(World world, Vec3d origin, Vec3d range, int age) {
        SparkleFX fx = Wizardry.proxy.spawnParticleSparkle(world, origin, range);
        if (ThreadLocalRandom.current().nextInt(Wizardry.proxy.getParticleDensity() / 100) <= 1) {
            fx.setMaxAge(age * Wizardry.proxy.getParticleDensity() / 100);
        } else fx.setMaxAge(0);
        return fx;
    }
}
