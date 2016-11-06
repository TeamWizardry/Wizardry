package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/10/2016.
 */
public class TileCraftingPlate extends TileMod implements ITickable, IManaSink, IStructure {

	public int craftingTimeLeft = 500;
	@Save
	public int tick;
	@Save
	public boolean isCrafting;
	@Save
	public int craftingTime = 500;
	@Save
	@Nullable
	public ItemStack output;
	public List<ClusterObject> inventory = new ArrayList<>();
	public Random random = new Random(getPos().toLong());

	private static List<ItemStack> condenseItemList(List<ItemStack> list) {
		if (list.isEmpty()) return null;
		List<ItemStack> items = new ArrayList<>();
		items.add(list.remove(0));
		while (!list.isEmpty()) {
			if (ItemStack.areItemStacksEqual(list.get(0), items.get(items.size() - 1)))
				items.get(items.size() - 1).stackSize += list.remove(0).stackSize;
			else
				items.add(list.remove(0));
		}
		return items;
	}

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		inventory.clear();
		NBTTagList list = compound.getTagList("clusters", NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			ClusterObject cluster = new ClusterObject();
			cluster.deserializeNBT(list.getCompoundTagAt(i));
			inventory.add(cluster);
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound) {
		NBTTagList list = new NBTTagList();
		for (ClusterObject cluster : inventory) list.appendTag(cluster.serializeNBT());
		compound.setTag("clusters", list);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;
		if (tick < 360) tick += 10;
		else tick = 0;
		if (!inventory.isEmpty()) {
			inventory.forEach(ClusterObject::tick);
			worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
		}
		markDirty();

		if (isCrafting && (output != null)) {
			LibParticles.CRAFTING_ALTAR_HELIX(worldObj, new Vec3d(pos).addVector(0.5, 0.25, 0.5));
			if (!inventory.isEmpty())
				for (ClusterObject cluster : inventory) {
					if (((ThreadLocalRandom.current().nextInt(10)) != 0)) continue;
					LibParticles.CRAFTING_ALTAR_CLUSTER_SUCTION(worldObj, new Vec3d(pos).addVector(0.5, 0.5, 0.5), cluster.trail.reverse());
				}
		}

		if (!isCrafting && !inventory.isEmpty())
			for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 10); i++) {
				ClusterObject cluster = inventory.get(ThreadLocalRandom.current().nextInt(inventory.size()));
				LibParticles.CRAFTING_ALTAR_CLUSTER_DRAPE(worldObj, new Vec3d(pos).addVector(0.5, 0.5, 0.5).add(cluster.current));
			}

		if ((output == null) && !isCrafting && !inventory.isEmpty() && (inventory.get(inventory.size() - 1).stack.getItem() instanceof Infusable)) {
			isCrafting = true;
			craftingTimeLeft = 500;
			output = inventory.remove(inventory.size() - 1).stack;
		}

		if (isCrafting) {
			if (craftingTimeLeft > 0) --craftingTimeLeft;
			else {
				isCrafting = false;

				LibParticles.CRAFTING_ALTAR_PEARL_EXPLODE(worldObj, new Vec3d(pos).addVector(0.5, 1, 0.5));

				if (!inventory.isEmpty()) {
					for (ClusterObject cluster : inventory)
						LibParticles.CRAFTING_ALTAR_CLUSTER_EXPLODE(worldObj, new Vec3d(pos).addVector(0.5, 0.5, 0.5).add(cluster.current));
					inventory.clear();
				}
			/*List<ItemStack> condensed = condenseItemList(CapsUtils.getListOfItems(inventory).stream().collect(Collectors.toList()));
			Parser spellParser = new Parser(condensed);
			Module parsedSpell = null;
			try {
				while (parsedSpell == null) parsedSpell = spellParser.parseInventoryToModule();
			} catch (NoSuchElementException ignored) {
			}*/

				//if (parsedSpell != null) {
				ItemStack stack = new ItemStack(ModItems.PEARL_NACRE);
				//	ItemNBTHelper.setString(stack, "type", PearlType.INFUSED.toString());
				//	ItemNBTHelper.setCompound(stack, NBT.SPELL, parsedSpell.getModuleData());
				output = stack;
				craftingTimeLeft = craftingTime;
				//} else System.err.println("Something went wrong! @" + dest);
			}
		}
	}

	public static class ClusterObject implements INBTSerializable<NBTTagCompound> {

		public ItemStack stack;
		public Vec3d dest;
		public Vec3d current;
		public InterpBezier3D trail;
		public double tick;
		public boolean reverse;
		public double speedMultiplier;
		private float queue;

		public ClusterObject(ItemStack stack, Random random) {
			double extent = 10.0;
			double theta = 2.0f * (float) Math.PI * random.nextFloat();
			double r = extent * random.nextFloat();
			double x1 = r * MathHelper.cos((float) theta);
			double z1 = r * MathHelper.sin((float) theta);

			dest = new Vec3d(x1, 5 + (random.nextFloat() * 3), z1);
			this.stack = stack;
			current = Vec3d.ZERO;
			trail = new InterpBezier3D(Vec3d.ZERO, dest);
			reverse = random.nextBoolean();
			speedMultiplier = (2.0f * (random.nextFloat() - 0.5f)) * 3;
		}

		public ClusterObject() {
		}

		public void tick() {
			current = trail.get((queue < 1.0f) ? (queue += 0.01f) : 1.0f);
			if (tick < 360) tick += 1 * speedMultiplier;
			else tick = 0;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setTag("stack", stack.serializeNBT());
			compound.setDouble("dest_x", dest.xCoord);
			compound.setDouble("dest_y", dest.yCoord);
			compound.setDouble("dest_z", dest.zCoord);
			compound.setDouble("current_x", current.xCoord);
			compound.setDouble("current_y", current.yCoord);
			compound.setDouble("current_z", current.zCoord);
			compound.setDouble("speed_multiplier", speedMultiplier);
			compound.setFloat("queue", queue);
			compound.setDouble("tick", tick);
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"));
			dest = new Vec3d(nbt.getDouble("dest_x"), nbt.getDouble("dest_y"), nbt.getDouble("dest_z"));
			current = new Vec3d(nbt.getDouble("current_x"), nbt.getDouble("current_y"), nbt.getDouble("current_z"));
			speedMultiplier = nbt.getDouble("speed_multiplier");
			queue = nbt.getFloat("queue");
			tick = nbt.getDouble("tick");
			trail = new InterpBezier3D(Vec3d.ZERO, dest);
		}
	}
}
