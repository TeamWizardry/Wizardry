package com.teamwizardry.wizardry.api.trackers;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/27/2016.
 */
public class BookTrackerObject {
    private double x, y, z;
    private World world;
    private EntityItem item;
    private int countdown;
    private boolean startCountDown = false;

    public BookTrackerObject(EntityItem entityItem) {
        item = entityItem;
        world = entityItem.worldObj;
        startCountDown = true;
        countdown = 0;
        x = entityItem.posX;
        y = entityItem.posY;
        z = entityItem.posZ;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public EntityItem getItem() {
        return item;
    }

    public boolean itemExists() {
        return item != null;
    }

    public boolean isStartCountDown() {
        return startCountDown;
    }

    public void setStartCountDown(boolean startCountDown) {
        this.startCountDown = startCountDown;
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
}
