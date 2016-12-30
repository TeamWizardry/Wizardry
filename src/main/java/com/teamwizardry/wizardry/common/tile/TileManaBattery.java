package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.common.fluid.FluidBlockMana;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@TileRegister("mana_battery")
public class TileManaBattery extends TileMod implements ITickable, IManaSink {

	public int maxMana = 1000000;
	@Save
	public int currentMana;

	@Override
	public void update() {
		Random rand = new Random();
		int chance = rand.nextInt(50);
		if (chance != 1) return;

		int x = rand.nextInt(3) - 1;
		int z = rand.nextInt(3) - 1;
		BlockPos pos = getPos().add(x, -2, z);
        if (world.getBlockState(pos) == FluidBlockMana.instance.getDefaultState()) {
            currentMana += 1000;
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }

		List<BlockPos> pedestals = new ArrayList<>();
		for (int i = -6; i < 6; i++) {
			for (int j = -6; j < 6; j++) {

				BlockPos pedPos = new BlockPos(pos.getX() + i, pos.getY() - 2, pos.getZ() + j);
				if (pedestals.contains(pedPos)) continue;
                IBlockState block = world.getBlockState(pedPos);
                if (block.getBlock() != ModBlocks.PEDESTAL) continue;
                TilePedestal pedestal = (TilePedestal) world.getTileEntity(pedPos);
                if (pedestal == null) return;
				if (pedestal.pearl == null) return;

				BlockPos oppPos = new BlockPos(pos.getX() - i, pedPos.getY(), pos.getZ() - j);
				if (pedestals.contains(oppPos)) continue;
                IBlockState oppBlock = world.getBlockState(oppPos);
                if (oppBlock.getBlock() != ModBlocks.PEDESTAL) return;
                TilePedestal oppPed = (TilePedestal) world.getTileEntity(oppPos);
                if (oppPed == null) return;
				if (oppPed.pearl == null) return;

				pedestals.add(pedPos);
				pedestals.add(oppPos);
			}
		}
	}
}
