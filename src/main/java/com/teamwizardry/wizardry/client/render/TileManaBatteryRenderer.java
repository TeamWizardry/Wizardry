package com.teamwizardry.wizardry.client.render;

import com.teamwizardry.librarianlib.common.util.math.Matrix4;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.common.tile.TilePedestal;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LordSaad.
 */
public class TileManaBatteryRenderer extends TileEntitySpecialRenderer<TileManaBattery> {

	@Override
	public void renderTileEntityAt(TileManaBattery te, double x, double y, double z, float partialTicks, int destroyStage) {
		World world = te.getWorld();
		BlockPos pos = te.getPos();

		List<BlockPos> pedestals = new ArrayList<>();
		Set<BlockPos> poses = new HashSet<>();
		for (int i = 0; i < 360; i++) {
			double angle = Math.toRadians(i) * Math.PI * 2;
			double cX = 0.5 + Math.cos(angle) * 6;
			double cZ = 0.5 + Math.sin(angle) * 6;
			BlockPos pedPos = new BlockPos(pos.getX() + cX, pos.getY() - 2, pos.getZ() + cZ);

			poses.add(pedPos);
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
				poses.remove(oppPos);
				if (ThreadLocalRandom.current().nextInt(10) == 0)
					LibParticles.MAGIC_DOT(world, new Vec3d(oppPos).addVector(0.5, 0.5, 0.5), (float) ThreadLocalRandom.current().nextDouble(1, 4));
				continue;
			}
			TilePedestal oppPed = (TilePedestal) world.getTileEntity(oppPos);
			if (oppPed == null) continue;
			if (oppPed.pearl == null) continue;

			pedestals.add(pedPos);
			pedestals.add(oppPos);
		}

		for (BlockPos pedPos : poses)
			if (ThreadLocalRandom.current().nextInt(5) == 0)
				LibParticles.MAGIC_DOT(world, new Vec3d(pedPos).addVector(0.5, 0.5, 0.5), -1);

		for (BlockPos pedPos : pedestals)
			if (ThreadLocalRandom.current().nextBoolean())
				LibParticles.COLORFUL_BATTERY_BEZIER(world, pedPos, pos);
	}
}
