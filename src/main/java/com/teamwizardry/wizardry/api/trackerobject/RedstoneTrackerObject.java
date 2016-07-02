package com.teamwizardry.wizardry.api.trackerobject;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by Saad on 6/27/2016.
 */
public class RedstoneTrackerObject {

    private Vec3d pos;
    private World world;
    private int countdown, queue;
    private ArrayList<Vec3d> helix;
    private EntityItem redstone, vinteum;
    private boolean startCountdown = false, hasAdjusted = false, hasVinteumSpawned = false;
    private int stackSize = 1;

    public RedstoneTrackerObject(EntityItem entityItem) {
        stackSize = entityItem.getEntityItem().stackSize;
        this.redstone = entityItem;
        world = entityItem.worldObj;
        pos = new Vec3d(entityItem.posX, entityItem.posY, entityItem.posZ);
        countdown = 0;
        queue = 0;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public Vec3d getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public void setPos(Vec3d pos) {
        this.pos = pos;
    }

    public int getStackSize() {
        return stackSize;
    }

    public World getWorld() {
        return world;
    }

    public EntityItem getRedstone() {
        return redstone;
    }

    public boolean isStartCountdown() {
        return startCountdown;
    }

    public void setStartCountdown(boolean startCountdown) {
        this.startCountdown = startCountdown;
    }

    public boolean hasAdjusted() {
        return hasAdjusted;
    }

    public void setHasAdjusted(boolean hasAdjusted) {
        this.hasAdjusted = hasAdjusted;
    }

    public ArrayList<Vec3d> getHelix() {
        return helix;
    }

    public void setHelix(ArrayList<Vec3d> helix) {
        this.helix = helix;
    }

    public boolean hasVinteumSpawned() {
        return hasVinteumSpawned;
    }

    public void setHasVinteumSpawned(boolean hasVinteumSpawned) {
        this.hasVinteumSpawned = hasVinteumSpawned;
    }

    public EntityItem getVinteum() {
        return vinteum;
    }

    public void setVinteum(EntityItem vinteum) {
        this.vinteum = vinteum;
    }

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }
}
