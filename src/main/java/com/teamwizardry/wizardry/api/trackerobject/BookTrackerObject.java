package com.teamwizardry.wizardry.api.trackerobject;

import com.teamwizardry.librarianlib.api.util.math.MathShapes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by Saad on 6/27/2016.
 */
public class BookTrackerObject {
    private double x, y, z, increment;
    private ArrayList<Vec3d> helix, circle;
    private World world;
    private int countdown, queue;

    public BookTrackerObject(EntityItem entityItem) {
        world = entityItem.worldObj;
        countdown = 0;
        queue = 0;
        increment = 0;
        x = entityItem.posX;
        y = entityItem.posY;
        z = entityItem.posZ;
        helix = MathShapes.createHelix(new Vec3d(entityItem.posX, entityItem.posY, entityItem.posZ));
        circle = MathShapes.createCircle(new Vec3d(entityItem.posX, entityItem.posY, entityItem.posZ), 2, 100);
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public World getWorld() {
        return world;
    }

    public ArrayList<Vec3d> getHelix() {
        return helix;
    }

    public void setHelix(ArrayList<Vec3d> helix) {
        this.helix = helix;
    }

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public ArrayList<Vec3d> getCircle() {
        return circle;
    }

    public double getIncrement() {
        return increment;
    }

    public void setIncrement(double increment) {
        this.increment = increment;
    }
}
