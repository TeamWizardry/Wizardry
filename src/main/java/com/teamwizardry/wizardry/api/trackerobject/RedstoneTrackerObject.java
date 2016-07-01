package com.teamwizardry.wizardry.api.trackerobject;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/27/2016.
 */
public class RedstoneTrackerObject {

    private BlockPos pos;
    private World world;
    private EntityItem item;
    private int countdown;
    private boolean startCountDown = false;
    private int stackSize = 1;

    public RedstoneTrackerObject(EntityItem entityItem) {
        item = entityItem;
        stackSize = entityItem.getEntityItem().stackSize;
        countdown = 0;
        world = entityItem.worldObj;
        pos = entityItem.getPosition();
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
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

    public int getStackSize() {
        return stackSize;
    }

    public World getWorld() {
        return world;
    }
}
