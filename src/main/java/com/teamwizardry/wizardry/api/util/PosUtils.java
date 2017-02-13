package com.teamwizardry.wizardry.api.util;

import com.teamwizardry.wizardry.common.tile.TileStaff;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Saad on 8/27/2016.
 */
public final class PosUtils {

	private static final EnumFacing[] northSouth = {EnumFacing.NORTH, EnumFacing.SOUTH};
	private static final EnumFacing[] eastWest = {EnumFacing.EAST, EnumFacing.WEST};
	private static final EnumFacing[] upDown = {EnumFacing.UP, EnumFacing.DOWN};

	public static BlockPos checkNeighbor(World world, BlockPos origin, Block desiredBlockToFind) {
		if (world.getBlockState(origin).getBlock() == desiredBlockToFind) return origin;

		for (EnumFacing vertical : upDown) {
			BlockPos pos = origin.offset(vertical);
			if (world.getBlockState(pos).getBlock() == desiredBlockToFind) return pos;
		}

		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos pos = origin.offset(facing);
			if (world.getBlockState(pos).getBlock() == desiredBlockToFind) return pos;

			for (EnumFacing vertical : upDown) {
				BlockPos pos2 = pos.offset(vertical);
				if (world.getBlockState(pos2).getBlock() == desiredBlockToFind) return pos2;
			}
		}

		for (EnumFacing diagonal1 : northSouth) {
			BlockPos pos1 = origin.offset(diagonal1);
			for (EnumFacing diagnonal2 : eastWest) {
				BlockPos pos2 = pos1.offset(diagnonal2);
				if (world.getBlockState(pos2).getBlock() == desiredBlockToFind) return pos2;

				for (EnumFacing vertical : upDown) {
					BlockPos pos3 = pos2.offset(vertical);
					if (world.getBlockState(pos3).getBlock() == desiredBlockToFind) return pos3;
				}
			}
		}
		return origin;
	}

	public static class ManaBatteryPositions {

		public Set<BlockPos> takenPoses = new HashSet<>();
		public Set<BlockPos> missingSymmetry = new HashSet<>();
		public Set<BlockPos> fullCircle = new HashSet<>();

		public ManaBatteryPositions(World world, BlockPos battery) {
			for (int i = 0; i < 360; i++) {
				double angle = Math.toRadians(i);
				double cX = Math.cos(angle) * 6;
				double cZ = Math.sin(angle) * 6;
				BlockPos staffPos = new BlockPos(battery.getX() + cX + 0.5, battery.getY() - 2, battery.getZ() + cZ + 0.5);

				if (world.getBlockState(staffPos.down()).getBlock() != ModBlocks.WISDOM_WOOD_PIGMENTED_PLANKS) continue;
				fullCircle.add(staffPos);

				if (takenPoses.contains(staffPos)) continue;
				IBlockState block = world.getBlockState(staffPos);
				if (block.getBlock() != ModBlocks.STAFF_BLOCK) continue;
				TileStaff staff = (TileStaff) world.getTileEntity(staffPos);
				if (staff == null) continue;
				if (staff.pearl == null) continue;
				fullCircle.remove(staffPos);

				int j = (180 + i) % 360;
				double newAngle = Math.toRadians(j);
				double oppX = 0.5 + Math.cos(newAngle) * 6;
				double oppZ = 0.5 + Math.sin(newAngle) * 6;
				BlockPos oppPos = new BlockPos(battery.getX() + oppX + 0.5, battery.getY() - 2, battery.getZ() + oppZ + 0.5);

				if (world.getBlockState(oppPos.down()).getBlock() != ModBlocks.WISDOM_WOOD_PIGMENTED_PLANKS) {
					missingSymmetry.add(oppPos);
					continue;
				}
				if (takenPoses.contains(oppPos)) {
					missingSymmetry.add(oppPos);
					continue;
				}
				IBlockState oppBlock = world.getBlockState(oppPos);
				if (oppBlock.getBlock() != ModBlocks.STAFF_BLOCK) {
					missingSymmetry.add(oppPos);
					continue;
				}
				TileStaff oppPed = (TileStaff) world.getTileEntity(oppPos);
				if (oppPed == null) {
					missingSymmetry.add(oppPos);
					continue;
				}
				if (oppPed.pearl == null) {
					missingSymmetry.add(oppPos);
					continue;
				}

				takenPoses.add(staffPos);
				takenPoses.add(oppPos);
			}
		}
	}
}
