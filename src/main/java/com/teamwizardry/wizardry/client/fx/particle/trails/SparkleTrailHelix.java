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
    private Vec3d center, addMotion = new Vec3d(0, 0, 0);

    public SparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius, double initialTheta, int age, boolean fade) {
        super(worldIn, origin.xCoord, origin.yCoord, origin.zCoord, 1f, 0.5f, 50, false);
        this.center = center;
        this.radius = radius;
        this.theta = initialTheta;
        this.particleMaxAge = age;
        setFadeOut(fade);
    }

    public SparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius, int age, boolean fade) {
        super(worldIn, origin.xCoord, origin.yCoord, origin.zCoord, 1f, 0.5f, 50, false);
        this.center = center;
        this.radius = radius;
        this.theta = ThreadLocalRandom.current().nextDouble(0, 360);
        this.particleMaxAge = age;
        setFadeOut(fade);
    }

    public void addContinuousMotion(Vec3d addMotion) {
        this.addMotion = addMotion;
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

        double x = center.xCoord + radius * Math.cos(theta);
        double y = center.yCoord + radius * Math.sin(theta) * Math.cos(theta);
        double z = center.zCoord + radius * Math.sin(theta);
        motionX = (x - prevPosX) / 2 + addMotion.xCoord;
        motionY = (y - prevPosY) / 2 + addMotion.yCoord;
        motionZ = (z - prevPosZ) / 2 + addMotion.zCoord;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }
}
