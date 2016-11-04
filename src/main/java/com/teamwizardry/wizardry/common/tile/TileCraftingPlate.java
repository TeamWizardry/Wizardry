package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.client.fx.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.client.fx.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants.MISC;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.api.item.PearlType;
import com.teamwizardry.wizardry.api.util.CapsUtils;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/10/2016.
 */
public class TileCraftingPlate extends TileMod implements ITickable, IManaSink, IStructure {

	@Save
	public int tick;
	@Save
	public boolean isCrafting;
	@Save
	public int craftingTime = 500;
	public int craftingTimeLeft = 500;
	@Save
	public ItemStackHandler output = new ItemStackHandler(1) {
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return (!isCrafting && (output.getStackInSlot(0) != null)) ? super.extractItem(slot, amount, simulate) : null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return stack;
		}
	};
	@Save
	public ItemStackHandler inventory = new ItemStackHandler(54) {
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return (isCrafting || (output.getStackInSlot(0) != null)) ? null : super.extractItem(slot, amount, simulate);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (stack.getItem() instanceof Infusable) {
				Infusable pearl = (Infusable) stack.getItem();
				if (pearl.getType(stack) == PearlType.MUNDANE) {
					isCrafting = true;
					craftingTime = craftingTimeLeft;
				}
			}
			return (isCrafting || (output.getStackInSlot(0) != null)) ? null : super.insertItem(slot, stack, simulate);
		}
	};

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
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (Objects.equals(capability, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return (Objects.equals(capability, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) ? ((T) ((facing == EnumFacing.DOWN) ? output : inventory)) : super.getCapability(capability, facing);
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
//if (Structures.craftingAltar.match(worldObj, pos).getNonAirErrors().isEmpty()) return;

		if (isCrafting) {
			if (craftingTimeLeft > 0) --craftingTimeLeft;
			else isCrafting = false;

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
			output.setStackInSlot(0, stack);
				CapsUtils.clearInventory(inventory);
				craftingTimeLeft = craftingTime;
			//} else System.err.println("Something went wrong! @" + pos);
		}
	}
}
