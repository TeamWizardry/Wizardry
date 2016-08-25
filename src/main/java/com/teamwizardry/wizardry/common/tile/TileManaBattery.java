package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.wizardry.api.block.IManaAcceptor;
import com.teamwizardry.wizardry.common.fluid.FluidBlockMana;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Random;

public class TileManaBattery extends TileEntity implements ITickable, IManaAcceptor {

	public int MAX_MANA = 1000000;
	public int current_mana = 0;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("mana")) current_mana = compound.getInteger("mana");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("mana", current_mana);
		return compound;
	}

	@Override
	public void update() {
		Random rand = new Random();
		int chance = rand.nextInt(50);
		if (chance != 1) return;

		int x = rand.nextInt(3) - 1;
		int z = rand.nextInt(3) - 1;
		BlockPos pos = this.getPos().add(x, -2, z);
		if (worldObj.getBlockState(pos) == FluidBlockMana.instance.getDefaultState()) {
			this.current_mana += 1000;
			worldObj.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}

		ArrayList<BlockPos> pedestals = new ArrayList<>();
		for (int i = -6; i < 6; i++) {
			for (int j = -6; j < 6; j++) {

				BlockPos pedPos = new BlockPos(pos.getX() + i, pos.getY() - 2, pos.getZ() + j);
				IBlockState block = worldObj.getBlockState(pedPos);
				if (block.getBlock() != ModBlocks.PEDESTAL) continue;
				TilePedestal pedestal = (TilePedestal) worldObj.getTileEntity(pedPos);
				if (pedestals.contains(pedPos)) continue;
				if (pedestal == null) return;
				if (pedestal.getManaPearl() == null) return;

				BlockPos oppPos = new BlockPos(pos.getX() - i, pedPos.getY(), pos.getZ() - j);
				IBlockState oppBlock = worldObj.getBlockState(oppPos);
				if (oppBlock.getBlock() != ModBlocks.PEDESTAL) return;
				TilePedestal oppPed = (TilePedestal) worldObj.getTileEntity(oppPos);
				if (pedestals.contains(oppPos)) continue;
				if (oppPed == null) return;
				if (oppPed.getManaPearl() == null) return;

				pedestals.add(pedPos);
				pedestals.add(oppPos);
			}
		}
	}
}
