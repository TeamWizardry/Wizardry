package com.teamwizardry.wizardry.api.render;

import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.tile.TileCraftingPlate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by Demoniaque.
 */
public class ClusterObject implements INBTSerializable<NBTTagCompound> {

	public ItemStack stack;
	public Vec3d dest, origin;
	public long worldTime;
	public double destTime;
	public double angle;
	public int tick = 0;
	private TileCraftingPlate plate;

	public ClusterObject(TileCraftingPlate plate, ItemStack stack, World world, @Nullable Vec3d origin) {
		this.plate = plate;
		this.stack = stack;

		if (origin != null) {
			this.origin = origin;
			dest = new Vec3d(0, 0.5, 0);
		} else {
			this.origin = new Vec3d(0, 0.5, 0);
			dest = new Vec3d(0, 1.5, 0);
		}

		worldTime = world.getTotalWorldTime();
		destTime = RandUtil.nextDouble(10, 30);
	}

	public ClusterObject(TileCraftingPlate plate) {
		this.plate = plate;
	}

	public void tick(World world, Random random) {
		tick++;
		if ((world.getTotalWorldTime() - worldTime) >= destTime) {
			CapManager manager = new CapManager(plate.getWizardryCap());
			if (manager.isManaEmpty()) {
				origin = dest;
				dest = new Vec3d(RandUtil.nextDouble(-0.3, 0.3), RandUtil.nextDouble(0.5, 0.6), RandUtil.nextDouble(-0.3, 0.3));
				worldTime = world.getTotalWorldTime();
				destTime = RandUtil.nextDouble(10, 30);
				return;
			}

			ItemStack input = plate.inputPearl.getHandler().getStackInSlot(0);
			CapManager manager1 = new CapManager(input);
			double t = 1 - (manager1.getMana() / manager1.getMaxMana());

			double radius = RandUtil.nextDouble(5, 8) * t;

			angle += RandUtil.nextDouble(-1.5, 1.5);
			double x = MathHelper.cos((float) angle) * radius;
			double z = MathHelper.sin((float) angle) * radius;

			Vec3d newDest = new Vec3d(x, (2 + (random.nextFloat() * 7)) * t, z);
			origin = dest;
			dest = newDest;
			worldTime = world.getTotalWorldTime();
			destTime = RandUtil.nextDouble(10, 30) * t;
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		if (stack != null && !stack.isEmpty()) compound.setTag("stack", stack.serializeNBT());
		if (dest != null) {
			compound.setDouble("dest_x", dest.x);
			compound.setDouble("dest_y", dest.y);
			compound.setDouble("dest_z", dest.z);
		}
		if (origin != null) {
			compound.setDouble("origin_x", origin.x);
			compound.setDouble("origin_y", origin.y);
			compound.setDouble("origin_z", origin.z);
		}
		compound.setDouble("dest_time", destTime);
		compound.setLong("world_time", worldTime);
		compound.setDouble("maxTick", tick);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("stack")) stack = new ItemStack(nbt.getCompoundTag("stack"));
		else stack = ItemStack.EMPTY;

		if (nbt.hasKey("dest_x") && nbt.hasKey("dest_y") && nbt.hasKey("dest_z"))
			dest = new Vec3d(nbt.getDouble("dest_x"), nbt.getDouble("dest_y"), nbt.getDouble("dest_z"));
		else
			dest = new Vec3d(RandUtil.nextDouble(-0.3, 0.3), RandUtil.nextDouble(0.5, 0.6), RandUtil.nextDouble(-0.3, 0.3));

		if (nbt.hasKey("origin_x") && nbt.hasKey("origin_y") && nbt.hasKey("origin_z"))
			origin = new Vec3d(nbt.getDouble("origin_x"), nbt.getDouble("origin_y"), nbt.getDouble("origin_z"));
		else
			origin = new Vec3d(RandUtil.nextDouble(-0.3, 0.3), RandUtil.nextDouble(0.5, 0.6), RandUtil.nextDouble(-0.3, 0.3));

		if (nbt.hasKey("dest_time")) destTime = nbt.getDouble("dest_time");
		else destTime = 0;

		if (nbt.hasKey("world_time")) worldTime = nbt.getLong("world_time");
		else worldTime = 0;

		if (nbt.hasKey("maxTick")) tick = nbt.getInteger("maxTick");
		else tick = 0;
	}
}
