package com.teamwizardry.wizardry.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RayTrace {

	private final World world;
	private final Vec3d slope;
	private final Vec3d origin;
	private final double range;

	private boolean skipBlocks = false;
	private boolean skipUncollidableBlocks = false;
	private boolean skipEntities = false;
	@Nullable
	private Entity skipEntity = null;

	public RayTrace(World world, Vec3d slope, Vec3d origin, double range) {
		this.world = world;
		this.slope = slope;
		this.origin = origin;
		this.range = range;
	}

	public RayTrace setSkipEntity(@Nullable Entity skipEntity) {
		this.skipEntity = skipEntity;
		return this;
	}

	public RayTrace setSkipBlocks(boolean skipBlocks) {
		this.skipBlocks = skipBlocks;
		return this;
	}

	public RayTrace setSkipUncollidableBlocks(boolean skipUncollidableBlocks) {
		this.skipUncollidableBlocks = skipUncollidableBlocks;
		return this;
	}

	public RayTrace setSkipEntities(boolean skipEntities) {
		this.skipEntities = skipEntities;
		return this;
	}

	/**
	 * Credits to Masa on discord for providing the base of the code. I heavily modified it.
	 * This raytracer will precisely trace entities and blocks (including misses) without snapping to any grid.
	 *
	 * @return The RaytraceResult.
	 */
	public RayTraceResult trace() {
		Vec3d lookVec = origin.add(slope.scale(range));

		RayTraceResult result;
		if (skipBlocks) {
			result = new RayTraceResult(
					RayTraceResult.Type.BLOCK,
					lookVec,
					EnumFacing.getFacingFromVector((float) lookVec.x, (float) lookVec.y, (float) lookVec.z),
					new BlockPos(lookVec));
		} else result = world.rayTraceBlocks(origin, lookVec, false, skipUncollidableBlocks, skipUncollidableBlocks);

		if (skipEntities) return result;

		Entity targetEntity = null;
		RayTraceResult entityTrace = null;
		AxisAlignedBB bb = new AxisAlignedBB(lookVec.x, lookVec.y, lookVec.z, lookVec.x, lookVec.y, lookVec.z);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(skipEntity, bb.expand(range, range, range));
		double closest = 0.0D;

		for (Entity entity : list) {
			bb = entity.getEntityBoundingBox();
			RayTraceResult traceTmp = bb.calculateIntercept(lookVec, origin);

			if (traceTmp != null) {
				double tmp = origin.distanceTo(traceTmp.hitVec);

				if (tmp < closest || closest == 0.0D) {
					targetEntity = entity;
					entityTrace = traceTmp;
					closest = tmp;
				}
			}
		}

		if (targetEntity != null) result = new RayTraceResult(targetEntity, entityTrace.hitVec);

		return result;
	}
}