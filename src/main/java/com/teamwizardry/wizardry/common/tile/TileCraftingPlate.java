package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.api.item.PearlType;
import com.teamwizardry.wizardry.api.render.ClusterObject;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Saad on 6/10/2016.
 */
@TileRegister("crafting_plate")
public class TileCraftingPlate extends TileMod implements ITickable, IManaSink, IStructure {

	@Save
	public int craftingTime = 300;
	@Save
	public int craftingTimeLeft = 300;
	@Save
	public int tick;
	@Save
	public boolean isCrafting;
	@Save
	@Nullable
	public ItemStack output;
	@Save
	public boolean structureComplete;
	public List<ClusterObject> inventory = new ArrayList<>();
	public Random random = new Random(getPos().toLong());

	@Override
	public void readCustomNBT(NBTTagCompound compound) {
		inventory.clear();
		NBTTagList list = compound.getTagList("clusters", NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			ClusterObject cluster = new ClusterObject(this);
			cluster.deserializeNBT(list.getCompoundTagAt(i));
			inventory.add(cluster);
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound compound, boolean sync) {
		NBTTagList list = new NBTTagList();
		for (ClusterObject cluster : inventory) list.appendTag(cluster.serializeNBT());
		compound.setTag("clusters", list);
	}

	@NotNull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		if (world.isRemote) return;
		if (tick < 360) tick += 10;
		else tick = 0;
		if (!inventory.isEmpty()) {
			for (ClusterObject cluster : inventory) cluster.tick(world, random);
			world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		}
		markDirty();

		if ((output == null) && !isCrafting && !inventory.isEmpty() && (inventory.get(inventory.size() - 1).stack.getItem() instanceof Infusable)) {
			isCrafting = true;
			craftingTimeLeft = craftingTime;
			output = inventory.remove(inventory.size() - 1).stack;
			markDirty();
		}

		if (isCrafting) {
			if (craftingTimeLeft > 0) craftingTimeLeft--;
			else {
				isCrafting = false;
				markDirty();

				// TODO PACKET
				LibParticles.CRAFTING_ALTAR_PEARL_EXPLODE(world, new Vec3d(pos).addVector(0.5, 1, 0.5));

//                LibParticles.CRAFTING_ALTAR_CLUSTER_EXPLODE(te.getWorld(), new Vec3d(te.getPos()).addVector(0.5, 0.5, 0.5).add(current));


				List<ItemStack> stacks = new ArrayList<>();
				for (ClusterObject cluster : inventory) stacks.add(cluster.stack);
				SpellStack spellStack = new SpellStack(stacks);

				ItemStack stack = new ItemStack(ModItems.PEARL_NACRE);
				ItemNBTHelper.setString(stack, "type", PearlType.INFUSED.toString());

				NBTTagList list = new NBTTagList();
				for (Module module : spellStack.compiled) list.appendTag(module.serializeNBT());
				ItemNBTHelper.setList(stack, Constants.NBT.SPELL, list);

				output = stack;
				inventory.clear();
				craftingTimeLeft = craftingTime;
				markDirty();
			}
		} else {
			craftingTimeLeft = craftingTime;
			markDirty();
		}
	}

	@Override
	public String structureName() {
		return "crafting_altar";
	}
}
