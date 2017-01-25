package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.common.fluid.FluidBlockMana;
import com.teamwizardry.wizardry.common.network.PacketParticleAmbientFizz;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@TileRegister("mana_battery")
public class TileManaBattery extends TileMod implements ITickable, IManaSink {

	public int maxMana = 9000;
	@Save
	public int currentMana;

	@Override
	public void update() {
		if (!world.isRemote) return;

		Random rand = new Random();

		if (currentMana < maxMana) {
			int x = ThreadLocalRandom.current().nextInt(-5, 5);
			int z = ThreadLocalRandom.current().nextInt(-5, 5);
			BlockPos mana = getPos().add(x, -3, z);
			if (world.getBlockState(mana) == FluidBlockMana.instance.getDefaultState()) {
				PacketHandler.NETWORK.sendToAllAround(new PacketParticleAmbientFizz(new Vec3d(mana).addVector(0.5, 0.5, 0.5)), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 20));
				int chance = rand.nextInt(50);
				if (chance == 0) {
					if (currentMana > maxMana - 1000) currentMana = maxMana;
					else currentMana += 1000;
					world.setBlockState(mana, Blocks.AIR.getDefaultState(), 3);
				}
			}
		}

		List<BlockPos> pedestals = new ArrayList<>();
		for (int i = 0; i < 360; i++) {
			double angle = Math.toRadians(i) * Math.PI * 2;
			double cX = 0.5 + Math.cos(angle) * 6;
			double cZ = 0.5 + Math.sin(angle) * 6;
			BlockPos pedPos = new BlockPos(pos.getX() + cX, pos.getY() - 2, pos.getZ() + cZ);

			if (pedestals.contains(pedPos)) continue;
			IBlockState block = world.getBlockState(pedPos);
			if (block.getBlock() != ModBlocks.PEDESTAL) continue;
			TilePedestal pedestal = (TilePedestal) world.getTileEntity(pedPos);
			if (pedestal == null) return;
			if (pedestal.pearl == null) continue;

			Vec3d dist = new Vec3d(pos.subtract(pedPos));
			Matrix4 matrix = new Matrix4();
			matrix.rotate(Math.toRadians(180), dist);
			Vec3d oppVec = dist.add(new Vec3d(pos));

			BlockPos oppPos = new BlockPos(oppVec.xCoord, pedPos.getY(), oppVec.zCoord);
			if (pedestals.contains(oppPos)) continue;
			IBlockState oppBlock = world.getBlockState(oppPos);
			if (oppBlock.getBlock() != ModBlocks.PEDESTAL) {
				continue;
			}
			TilePedestal oppPed = (TilePedestal) world.getTileEntity(oppPos);
			if (oppPed == null) continue;
			if (oppPed.pearl == null) continue;

			pedestals.add(pedPos);
			pedestals.add(oppPos);
		}

	}
}
