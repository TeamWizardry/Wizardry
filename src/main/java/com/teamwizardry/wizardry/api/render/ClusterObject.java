package com.teamwizardry.wizardry.api.render;

import com.teamwizardry.wizardry.api.capability.WizardManager;
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
	public int tick = 0;
	private TileCraftingPlate plate;

	public ClusterObject(TileCraftingPlate plate, ItemStack stack, World world, Random random) {
		this.plate = plate;

		WizardManager manager = new WizardManager(plate.cap);
		if (manager.isManaEmpty()) {
			dest = Vec3d.ZERO;
			this.stack = stack;
			origin = Vec3d.ZERO;
			worldTime = world.getTotalWorldTime();
			return;
		}
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
		tick++;
		if ((world.getTotalWorldTime() - worldTime) >= destTime) {
			WizardManager manager = new WizardManager(plate.cap);
			if (manager.isManaEmpty()) {
				origin = dest;
				dest = Vec3d.ZERO;
				worldTime = world.getTotalWorldTime();
				destTime = ThreadLocalRandom.current().nextDouble(10, 30);
				return;
			}

			double t = (plate.craftingTimeLeft * 1.0) / plate.craftingTime;

			double radius = ThreadLocalRandom.current().nextDouble(5, 8) * t;

			angle += ThreadLocalRandom.current().nextDouble(-1.5, 1.5);
			double x = MathHelper.cos((float) angle) * radius;
			double z = MathHelper.sin((float) angle) * radius;

			Vec3d newDest = new Vec3d(x, (2 + (random.nextFloat() * 7)) * t, z);
			origin = dest;
			dest = newDest;
			worldTime = world.getTotalWorldTime();
			destTime = ThreadLocalRandom.current().nextDouble(10, 30) * t;
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		if (stack != null && !stack.isEmpty()) compound.setTag("stack", stack.serializeNBT());
		if (dest != null) {
			compound.setDouble("dest_x", dest.xCoord);
			compound.setDouble("dest_y", dest.yCoord);
			compound.setDouble("dest_z", dest.zCoord);
		}
		if (origin != null) {
			compound.setDouble("origin_x", origin.xCoord);
			compound.setDouble("origin_y", origin.yCoord);
			compound.setDouble("origin_z", origin.zCoord);
		}
		compound.setDouble("dest_time", destTime);
		compound.setLong("world_time", worldTime);
		compound.setDouble("tick", tick);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("stack")) stack = new ItemStack(nbt.getCompoundTag("stack"));
		else stack = ItemStack.EMPTY;

		if (nbt.hasKey("dest_x") && nbt.hasKey("dest_y") && nbt.hasKey("dest_z"))
			dest = new Vec3d(nbt.getDouble("dest_x"), nbt.getDouble("dest_y"), nbt.getDouble("dest_z"));
		else dest = Vec3d.ZERO;

		if (nbt.hasKey("origin_x") && nbt.hasKey("origin_y") && nbt.hasKey("origin_z"))
			origin = new Vec3d(nbt.getDouble("origin_x"), nbt.getDouble("origin_y"), nbt.getDouble("origin_z"));
		else origin = Vec3d.ZERO;

		if (nbt.hasKey("dest_time")) destTime = nbt.getDouble("dest_time");
		else destTime = 0;

		if (nbt.hasKey("world_time")) worldTime = nbt.getLong("world_time");
		else worldTime = 0;

		if (nbt.hasKey("tick")) tick = nbt.getInteger("tick");
		else tick = 0;
	}
}
