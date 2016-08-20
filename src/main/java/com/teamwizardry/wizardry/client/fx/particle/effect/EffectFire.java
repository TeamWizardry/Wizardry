package com.teamwizardry.wizardry.client.fx.particle.effect;

import com.teamwizardry.wizardry.api.fx.IEffect;
import com.teamwizardry.wizardry.client.fx.GlitterFactory;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;

/**
 * Created by saad4 on 23/7/2016.
 */
public class EffectFire implements IEffect {

    private int power;

    public EffectFire(int power) {
        if (power > 500) power = 500;
        this.power = power;
    }

    @Override
    public void spawn(World world, Vec3d pos) {
        for (int i = 0; i < 5 + power / 100; i++) {
            Color yellow = new Color(0xFFFF00);
            Color orange = new Color(0xFF8C00);
            Color orangeRed = new Color(0xFF4500);
            Color gray = new Color(0x696969);

            SparkleFX center = GlitterFactory.getInstance().createSparkle(world, pos, new Vec3d(0.01, 0.01, 0.01), 5 + power / 30);
            center.setColor(Color.WHITE);
            center.setScale(1.5f + power / 1000);
            center.setAlpha(0.2f + power / 1000);
            center.setFadeOut();
            center.setFadeIn();
            center.setRandomlyBlurred();
            center.setRandomDirection(0.01 + power / 1000, 0.03 + power / 1000, 0.01 + power / 1000);

            SparkleFX rim = GlitterFactory.getInstance().createSparkle(world, pos, new Vec3d(0.1, 0.1, 0.1), 13 + power / 30);
            rim.setColor(Color.RED);
            rim.setScale(1f + power / 1000);
            rim.setAlpha(0.5f + power / 1000);
            rim.setFadeOut();
            rim.setFadeIn();
            rim.setRandomlyBlurred();
            rim.setRandomDirection(0.1 + power / 1000, 0.3 + power / 1000, 0.1 + power / 1000);

            SparkleFX outerRim = GlitterFactory.getInstance().createSparkle(world, pos, new Vec3d(0.3, 0.3, 0.3), 10 + power / 30);
            outerRim.setColor(orange);
            outerRim.setScale(1f + power / 1000);
            outerRim.setAlpha(0.5f + power / 1000);
            outerRim.setFadeOut();
            outerRim.setFadeIn();
            outerRim.setRandomlyBlurred();
            outerRim.setRandomDirection(0.15 + power / 1000, 0.2 + power / 1000, 0.15 + power / 1000);

            SparkleFX outestRim = GlitterFactory.getInstance().createSparkle(world, pos, new Vec3d(0.4, 0.4, 0.4), 10 + power / 30);
            outestRim.setColor(gray);
            outestRim.setScale(1.5f + power / 1000);
            outestRim.setAlpha(0.2f + power / 1000);
            outestRim.setLerp(Color.BLACK);
            outestRim.setRandomlyBlurred();
            outestRim.setFadeOut();
            outestRim.setFadeIn();
            outestRim.setRandomDirection(0.2 + power / 1000, 0.3 + power / 1000, 0.2 + power / 1000);
            outestRim.setJitter(20, 0.2 + power / 1000, 0.3 + power / 1000, 0.2 + power / 1000);
        }
    }
}
