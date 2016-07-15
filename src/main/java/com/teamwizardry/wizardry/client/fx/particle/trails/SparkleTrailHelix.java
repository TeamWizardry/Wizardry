package com.teamwizardry.wizardry.client.fx.particle.trails;

import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 15/7/2016.
 */
public class SparkleTrailHelix extends SparkleFX {

    private double theta, radius;
    private Vec3d center;

    public SparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius, double initialTheta) {
        super(worldIn, origin.xCoord, origin.yCoord, origin.zCoord, 1f, 0.5f, 50, false);
        this.center = center;
        this.radius = radius;
        this.theta = initialTheta;
    }

    public SparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius) {
        super(worldIn, origin.xCoord, origin.yCoord, origin.zCoord, 1f, 0.5f, 50, false);
        this.center = center;
        this.radius = radius;
        this.theta = ThreadLocalRandom.current().nextDouble(0, 360);
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        theta += Math.toRadians(10);

        posX = center.xCoord + radius * Math.cos(theta);
        posY = center.yCoord + radius * Math.sin(theta) * Math.cos(theta);
        posZ = center.zCoord + radius * Math.sin(theta);
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }
}
