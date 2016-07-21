package com.teamwizardry.wizardry.client.helper;

import com.teamwizardry.librarianlib.math.shapes.Arc3D;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 13/7/2016.
 */
public class CraftingPlateItemStackHelper {

    private ItemStack stack;
    private ArrayList<Vec3d> points;
    private int queue = 0;
    private double positionTheta;
    private Vec3d point;
    private double maxX, maxY, maxZ;
    private double ticker = 0;

    public CraftingPlateItemStackHelper(ItemStack stack) {
        this.stack = stack;

        maxX = ThreadLocalRandom.current().nextDouble(0, 5);
        maxZ = ThreadLocalRandom.current().nextDouble(0, 5);
        maxY = ThreadLocalRandom.current().nextInt(1, 5);

        positionTheta = ThreadLocalRandom.current().nextDouble(0.01, 1);
        double theta = Math.PI * 2 * positionTheta;
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        point = new Vec3d(cosTheta * maxX, ThreadLocalRandom.current().nextDouble(2, 8), sinTheta * maxZ);

        points = new Arc3D(new Vec3d(0, 0, 0), point, (float) maxY, ThreadLocalRandom.current().nextInt(50, 100)).getPoints();
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public ArrayList<Vec3d> getPoints() {
        return points;
    }

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public double getPositionTheta() {
        return positionTheta;
    }

    public void setPositionTheta(double positionTheta) {
        this.positionTheta = positionTheta;
    }

    public void setPoint(Vec3d point) {
        this.point = point;
    }

    public Vec3d getPoint() {
        return point;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMaxY() {
        return maxY;
    }

    public void tick() {
        if (ticker >= 360) ticker = 0;
        else ticker += (maxX + maxY + maxZ) / 3;
    }

    public double getTick() {
        return ticker;
    }
}
