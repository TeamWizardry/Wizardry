package com.teamwizardry.wizardry.api.util;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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

	public static boolean isLyingInCone(Vec3d pointToCheck, Vec3d origin, Vec3d endVector, float aperture) {
		float[] x = new float[]{(float) pointToCheck.xCoord, (float) pointToCheck.yCoord, (float) pointToCheck.zCoord};
		float[] t = new float[]{(float) origin.xCoord, (float) origin.yCoord, (float) origin.zCoord};
		float[] b = new float[]{(float) endVector.xCoord, (float) endVector.yCoord, (float) endVector.zCoord};

		float halfAperture = aperture / 2.f;
		float[] apexToXVect = dif(t, x);
		float[] axisVect = dif(t, b);

		boolean isInInfiniteCone = dotProd(apexToXVect, axisVect) / magn(apexToXVect) / magn(axisVect)
				> Math.cos(halfAperture);

		return isInInfiniteCone && dotProd(apexToXVect, axisVect) / magn(axisVect) < magn(axisVect);

	}

	private static float dotProd(float[] a, float[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

	private static float[] dif(float[] a, float[] b) {
		return (new float[]{
				a[0] - b[0],
				a[1] - b[1],
				a[2] - b[2]
		});
	}

	private static float magn(float[] a) {
		return (float) (Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]));
	}
}
