package com.teamwizardry.wizardry.client.fx.particle.effect;

import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.fx.IEffect;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by saad4 on 23/7/2016.
 */
public class EffectFire implements IEffect {

    public EffectFire() {

    }

    @Override
    public void spawn(World world, Vec3d pos) {
        for (int i = 0; i < 5; i++) {
            Color yellow = Color.rgb(0xFFFF00);
            Color orange = Color.rgb(0xFF8C00);
            Color orangeRed = Color.rgb(0xFF4500);
            Color gray = Color.rgb(0x696969);

            SparkleFX center = Wizardry.proxy.spawnParticleSparkle(world, pos);
            center.setMaxAge(10);
            center.setScale(2);
            center.setAlpha(1);
            center.setFadeIn();
            center.setFadeOut();
            center.setGrow();
            center.setShrink();
            center.setColor(Color.WHITE);
            center.setRandomDirection(0.01, 0.05, 0.01);

            SparkleFX rim = Wizardry.proxy.spawnParticleSparkle(world, pos);
            rim.setMaxAge(20);
            center.setScale(1);
            rim.setAlpha(0.5f);
            rim.setColor(Color.RED);
            rim.setFadeOut();
            rim.setFadeIn();
            rim.setGrow();
            rim.setShrink();
            rim.setRandomDirection(0.03, 0.05, 0.03);

            SparkleFX outerRim = Wizardry.proxy.spawnParticleSparkle(world, pos, new Vec3d(0.3, 0.3, 0.3));
            outerRim.setColor(orange);
            center.setScale(0.5f);
            outerRim.setAlpha(0.5f);
            outerRim.setFadeOut();
            outerRim.setFadeIn();
            outerRim.setGrow();
            outerRim.setShrink();
            outerRim.setRandomDirection(0.05, 0.05, 0.05);

            SparkleFX outestRim = Wizardry.proxy.spawnParticleSparkle(world, pos, new Vec3d(0.4, 0.4, 0.4));
            outestRim.setColor(gray);
            center.setScale(1f);
            outerRim.setAlpha(0.5f);
            outestRim.setLerp(Color.BLACK);
            outestRim.setBlurred();
            outerRim.setFadeOut();
            outerRim.setFadeIn();
            outestRim.setGrow();
            outestRim.setShrink();
            outestRim.setRandomDirection(0.05, 0.15, 0.05);
            outestRim.setJitter(10, 0.01, 0.01, 0.01);
        }
    }
}
