package com.teamwizardry.wizardry.api.render;

import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class ClusterObject implements INBTSerializable<NBTTagCompound> {

	public ItemStack stack;
	public Vec3d dest, origin;
	public long worldTime;
	public double destTime;
	public double angle;
	private TileCraftingPlate plate;

	public ClusterObject(TileCraftingPlate plate, ItemStack stack, World world, Random random) {
		this.plate = plate;
		double angle = Math.toDegrees(Math.random() * Math.PI * 2);
		double x = MathHelper.cos((float) angle) * ThreadLocalRandom.current().nextDouble(6, 8);
		double z = MathHelper.sin((float) angle) * ThreadLocalRandom.current().nextDouble(6, 8);

		dest = new Vec3d(x, 5 + (random.nextFloat() * 3), z);
		this.stack = stack;
		origin = Vec3d.ZERO;
		worldTime = world.getTotalWorldTime();
		destTime = ThreadLocalRandom.current().nextDouble(10, 30);
	}

	public ClusterObject(TileCraftingPlate plate) {
		this.plate = plate;
	}

	public void tick(World world, Random random) {
		if ((world.getTotalWorldTime() - worldTime) >= destTime) {
			double t = (plate.craftingTimeLeft * 1.0) / plate.craftingTime;

			double radius = ThreadLocalRandom.current().nextDouble(7, 8) * t;

			angle += ThreadLocalRandom.current().nextDouble(-1, 1);
			double x = MathHelper.cos((float) angle) * radius;
			double z = MathHelper.sin((float) angle) * radius;

			Vec3d newDest = new Vec3d(x, (5 + (random.nextFloat() * 3)) * t, z);
			origin = dest;
			dest = newDest;
			worldTime = world.getTotalWorldTime();
			destTime = ThreadLocalRandom.current().nextDouble(10, 30);
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("stack", stack.serializeNBT());
		compound.setDouble("dest_x", dest.xCoord);
		compound.setDouble("dest_y", dest.yCoord);
		compound.setDouble("dest_z", dest.zCoord);
		compound.setDouble("origin_x", origin.xCoord);
		compound.setDouble("origin_y", origin.yCoord);
		compound.setDouble("origin_z", origin.zCoord);
		compound.setDouble("dest_time", destTime);
		compound.setLong("world_time", worldTime);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"));
		dest = new Vec3d(nbt.getDouble("dest_x"), nbt.getDouble("dest_y"), nbt.getDouble("dest_z"));
		origin = new Vec3d(nbt.getDouble("origin_x"), nbt.getDouble("origin_y"), nbt.getDouble("origin_z"));
		destTime = nbt.getDouble("dest_time");
		worldTime = nbt.getLong("world_time");
	}
}
