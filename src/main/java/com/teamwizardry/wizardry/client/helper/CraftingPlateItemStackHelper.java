package com.teamwizardry.wizardry.client.helper;

import com.teamwizardry.librarianlib.math.shapes.Arc3D;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

/**
 * Created by Saad on 13/7/2016.
 */
public class CraftingPlateItemStackHelper {

    private ItemStack stack;
    private ArrayList<Vec3d> points;
    private int queue = 0;

    public CraftingPlateItemStackHelper(ItemStack stack, BlockPos pos) {
        this.stack = stack;
        double angle = Math.random() * Math.PI * 2;
        Vec3d point1 = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5);
        Vec3d point2 = new Vec3d(pos.getX() + Math.cos(angle) * 3, pos.getY() + 3.0, pos.getZ() + Math.sin(angle) * 3);
        points = new Arc3D(point1, point1.add(new Vec3d(3, 3, 3)), 4, 50).getPoints();
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
}
