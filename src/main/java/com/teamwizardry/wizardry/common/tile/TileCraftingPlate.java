package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants.MISC;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
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
		markDirty();

		if (output != null) {
			ParticleBuilder fizz = new ParticleBuilder(10);
			fizz.setScale(0.3f);
			fizz.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
			fizz.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));

			Matrix4 matrix4 = new Matrix4();
			matrix4.rotate(1.5707963267948966, new Vec3d(1, 0, 0));
			matrix4.rotate(Math.toRadians(-tick), new Vec3d(0, 0, 1));
			ParticleSpawner.spawn(fizz, worldObj, new StaticInterp<>(new Vec3d(pos).addVector(0.5, 1, 0.5)), 10, 0, (aFloat, particleBuilder) -> {
				fizz.setColor(new Color(255, 255, 255, ThreadLocalRandom.current().nextInt(50, 150)));
				fizz.setLifetime(ThreadLocalRandom.current().nextInt(20, 30));
				fizz.setMotion(matrix4.apply(new Vec3d(0, ThreadLocalRandom.current().nextDouble(0.04, 0.08), ThreadLocalRandom.current().nextDouble(0.04, 0.08))));
			});
			ParticleBuilder fizz2 = new ParticleBuilder(10);
			fizz2.setScale(0.3f);
			fizz2.setRender(new ResourceLocation(Wizardry.MODID, MISC.SPARKLE_BLURRED));
			fizz2.setAlphaFunction(new InterpFadeInOut(0.1f, 0.3f));
		}
		//if (Structures.craftingAltar.match(worldObj, dest).getNonAirErrors().isEmpty()) return;

		if ((output == null) && !isCrafting && (!inventory.isEmpty() && (inventory.get(inventory.size() - 1).stack.getItem() instanceof Infusable))) {
			isCrafting = true;
			craftingTimeLeft = 500;
		}

		if (isCrafting) {
			if (craftingTimeLeft > 0) --craftingTimeLeft;
			else {
				isCrafting = false;

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
			inventory.clear();
			craftingTimeLeft = craftingTime;
			//} else System.err.println("Something went wrong! @" + dest);
		}
	}

	public static class ClusterObject implements INBTSerializable<NBTTagCompound> {

		public ItemStack stack;
		public Vec3d dest;
		public Vec3d origin;
		public Vec3d current;
		public InterpBezier3D trail;
		public int tick;
		public boolean reverse;
		public double speedMultiplier;
		private int queue;

		public ClusterObject(ItemStack stack, Vec3d origin, Random random) {
			double extent = 5.0;
			double theta = 2.0f * (float) Math.PI * random.nextFloat();
			double r = extent * random.nextFloat();
			double x1 = r * MathHelper.cos((float) theta);
			double z1 = r * MathHelper.sin((float) theta);

			dest = new Vec3d(x1, random.nextFloat() * 3, z1);
			this.stack = stack;
			this.origin = Vec3d.ZERO;
			current = dest;
			trail = new InterpBezier3D(origin, dest);
			reverse = random.nextBoolean();
			speedMultiplier = random.nextDouble();
		}

		public ClusterObject() {
		}

		public void tick() {


			List<Vec3d> points = trail.list(50);
			//current = points.get((points.size() < queue) ? queue++ : (points.size() - 1));


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
			compound.setDouble("speed_multiplier", speedMultiplier);
			compound.setInteger("queue", queue);
			compound.setInteger("tick", tick);
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"));
			dest = new Vec3d(nbt.getDouble("dest_x"), nbt.getDouble("dest_y"), nbt.getDouble("dest_z"));
			current = origin = new Vec3d(nbt.getDouble("origin_x"), nbt.getDouble("origin_y"), nbt.getDouble("origin_z"));
			speedMultiplier = nbt.getDouble("speed_multiplier");
			queue = nbt.getInteger("queue");
			tick = nbt.getInteger("tick");
			trail = new InterpBezier3D(origin, dest);
		}
	}
}
