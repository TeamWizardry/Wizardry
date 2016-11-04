package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.structure.StructureMatchResult;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.item.PearlType;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.util.CapsUtils;
import com.teamwizardry.wizardry.common.Structures;
import com.teamwizardry.wizardry.common.spell.parsing.Parser;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Saad on 6/10/2016.
 */
public class TileCraftingPlate extends TileMod implements ITickable, IManaSink {

	@Save
	public boolean structureComplete;
	@Save
	public boolean isCrafting;
	@Save
	public int craftingTime = 500;
	public int craftingTimeLeft = 500;
	@Nullable
	@Save
	public ItemStack pearl;
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
			return (isCrafting || (output.getStackInSlot(0) != null)) ? null : super.insertItem(slot, stack, simulate);
		}
	};

	private static List<ItemStack> condenseItemList(List<ItemStack> list) {
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
		return (Objects.equals(capability, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) ? (T) inventory : super.getCapability(capability, facing);
	}

	public void validateStructure() {
		Structures.reload();
		// TODO
		StructureMatchResult match = Structures.craftingAltar.match(worldObj, pos);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		if (worldObj.isRemote) return;
		if (!structureComplete) return;

		if (isCrafting) {
			if (craftingTimeLeft > 0) --craftingTimeLeft;
			else isCrafting = false;

			// TODO condenser is broken
			List<ItemStack> condensed = condenseItemList(CapsUtils.getListOfItems(inventory).stream().collect(Collectors.toList()));
			Parser spellParser = new Parser(condensed);
			Module parsedSpell = null;
			try {
				while (parsedSpell == null) parsedSpell = spellParser.parseInventoryToModule();
			} catch (NoSuchElementException ignored) {
			}

			if (parsedSpell != null) {
				ItemNBTHelper.setString(pearl, "type", PearlType.INFUSED.toString());
				ItemNBTHelper.setCompound(pearl, NBT.SPELL, parsedSpell.getModuleData());
				EntityItem pearlItem = new EntityItem(worldObj, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, pearl);
				pearlItem.setVelocity(0.0, 0.8, 0.0);
				pearlItem.forceSpawn = true;
				worldObj.spawnEntityInWorld(pearlItem);
				pearl = null;
				CapsUtils.clearInventory(inventory);
				craftingTimeLeft = craftingTime;
			} else System.err.println("Something went wrong! @" + pos);
		}
	}
}
