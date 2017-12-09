package com.teamwizardry.wizardry.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Created by Saad on 8/27/2016.
 */
public final class PosUtils {

	public static final ArrayList<EnumFacing> symmetricFacingValues = new ArrayList<>();

	static {
		symmetricFacingValues.add(EnumFacing.UP);
		symmetricFacingValues.add(EnumFacing.DOWN);
		symmetricFacingValues.add(EnumFacing.EAST);
		symmetricFacingValues.add(EnumFacing.WEST);
		symmetricFacingValues.add(EnumFacing.SOUTH);
		symmetricFacingValues.add(EnumFacing.NORTH);
	}

	@Nullable
	public static BlockPos checkNeighbor(World world, BlockPos origin, Block... desiredBlocksToFind) {
		IBlockState originState = world.getBlockState(origin);
		for (Block desiredBlockToFind : desiredBlocksToFind) {
			if (originState.getBlock() == desiredBlockToFind) return origin;
		}
		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos pos = origin.offset(facing);
			IBlockState state = world.getBlockState(pos);
			for (Block desiredBlockToFind : desiredBlocksToFind) {
				if (state.getBlock() == desiredBlockToFind) return pos;
			}
		}
		return null;
	}

	@Nullable
	public static BlockPos checkNeighborBlocksThoroughly(World world, BlockPos origin, Block desiredBlockToFind) {
		if (world.getBlockState(origin).getBlock() == desiredBlockToFind) return origin;

		for (EnumFacing facing : EnumFacing.values()) {
			BlockPos pos = origin.offset(facing);
			if (world.getBlockState(pos).getBlock() == desiredBlockToFind) return pos;

			BlockPos pos2 = pos.offset(EnumFacing.DOWN);
			if (world.getBlockState(pos2).getBlock() == desiredBlockToFind) return pos2;
		}
		return null;
	}

	public static Vec3d vecFromRotations(float rotationPitch, float rotationYaw) {
		return Vec3d.fromPitchYaw(rotationPitch, rotationYaw);
	}

	public static float[] vecToRotations(Vec3d vec) {
		float yaw = (float) MathHelper.atan2(vec.z, vec.x);
		float pitch = (float) Math.asin(vec.y / vec.lengthVector());
		return new float[]{(float) Math.toDegrees(pitch), (float) Math.toDegrees(yaw) + 90};
	}
}
