package com.teamwizardry.wizardry.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque on 8/27/2016.
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

	public static void boom(World world, Vec3d pos, @Nullable Entity excluded, double scale, boolean reverseDirection) {
		List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(excluded, new AxisAlignedBB(new BlockPos(pos)).grow(32, 32, 32));
		for (Entity entity1 : entityList) {
			double x = entity1.getDistance(pos.x, pos.y, pos.z) / 32.0;
			double magY;

			if (reverseDirection) magY = x;
			else magY = -x + 1;

			Vec3d dir = entity1.getPositionVector().subtract(pos).normalize().scale(reverseDirection ? -1 : 1).scale(magY).scale(scale);

			entity1.motionX += (dir.x);
			entity1.motionY += (dir.y);
			entity1.motionZ += (dir.z);
			entity1.fallDistance = 0;
			entity1.velocityChanged = true;

			if (entity1 instanceof EntityPlayerMP)
				((EntityPlayerMP) entity1).connection.sendPacket(new SPacketEntityVelocity(entity1));
		}
	}
}
