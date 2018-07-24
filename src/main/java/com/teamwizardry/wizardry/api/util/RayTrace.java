package com.teamwizardry.wizardry.api.util;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;

/**
 * Copied from Wizardry
 */
public class RayTrace {

	private final World world;
	private final Vec3d slope;
	private final Vec3d origin;
	private final double range;

	private boolean skipBlocks = false;
	private boolean skipEntities = false;
	private boolean returnLastUncollidableBlock = true;
	private boolean ignoreBlocksWithoutBoundingBoxes = false;
	private Predicate<Entity> predicateEntity;
	private Predicate<Block> predicateBlock;
	private HashSet<BlockPos> skipBlockList = new HashSet<>();

	public RayTrace(@Nonnull World world, @Nonnull Vec3d slope, @Nonnull Vec3d origin, double range) {
		this.world = world;
		this.slope = slope;
		this.origin = origin;
		this.range = range;
	}

	public RayTrace addBlockToSkip(@Nonnull BlockPos pos) {
		skipBlockList.add(pos);
		return this;
	}

	public RayTrace setEntityFilter(Predicate<Entity> predicate) {
		this.predicateEntity = predicate;
		return this;
	}

	public RayTrace setBlockFilter(Predicate<Block> predicate) {
		this.predicateBlock = predicate;
		return this;
	}

	public RayTrace setReturnLastUncollidableBlock(boolean returnLastUncollidableBlock) {
		this.returnLastUncollidableBlock = returnLastUncollidableBlock;
		return this;
	}

	public RayTrace setIgnoreBlocksWithoutBoundingBoxes(boolean ignoreBlocksWithoutBoundingBoxes) {
		this.ignoreBlocksWithoutBoundingBoxes = ignoreBlocksWithoutBoundingBoxes;
		return this;
	}

	public RayTrace setSkipEntities(boolean skipEntities) {
		this.skipEntities = skipEntities;
		return this;
	}

	private boolean isOrigin(BlockPos pos) {
		return (new BlockPos(origin) == pos);
	}

	/**
	 * Credits to Masa on discord for providing the base of the code. I heavily modified it.
	 * This raytracer will precisely trace entities and blocks (including misses) without snapping to any grid.
	 *
	 * @return The RaytraceResult.
	 */
	@Nonnull
	public RayTraceResult trace() {
		Vec3d lookVec = origin.add(slope.scale(range));

		RayTraceResult entityResult = null;
		RayTraceResult blockResult = null;// world.rayTraceBlocks(origin, lookVec, false, ignoreBlocksWithoutBoundingBoxes, returnLastUncollidableBlock);


		if (!skipEntities) {
			Entity targetEntity = null;
			RayTraceResult entityTrace = null;
			AxisAlignedBB bb = new AxisAlignedBB(origin.x, origin.y, origin.z, lookVec.x, lookVec.y, lookVec.z);
			List<Entity> list = world.getEntitiesWithinAABB(Entity.class, bb.grow(range, range, range), input -> {
				if (predicateEntity == null) return true;
				else return predicateEntity.test(input);
			});
			double closest = 0.0D;

			for (Entity entity : list) {
				if (entity == null) continue;

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

			if (targetEntity != null) entityResult = new RayTraceResult(targetEntity, entityTrace.hitVec);
		}

		if (!skipBlocks) blockResult = traceBlock(origin, lookVec);

		if (blockResult == null)
			blockResult = new RayTraceResult(
					RayTraceResult.Type.BLOCK,
					lookVec,
					EnumFacing.getFacingFromVector((float) lookVec.x, (float) lookVec.y, (float) lookVec.z),
					new BlockPos(lookVec));

		return (entityResult != null && origin.distanceTo(entityResult.hitVec) < origin.distanceTo(blockResult.hitVec)) ? entityResult : blockResult;
	}

	private RayTraceResult traceBlock(@Nonnull Vec3d start, @Nonnull Vec3d end) {
		RayTraceResult raytraceresult2 = null;

		int i = MathHelper.floor(end.x);
		int j = MathHelper.floor(end.y);
		int k = MathHelper.floor(end.z);
		int l = MathHelper.floor(start.x);
		int i1 = MathHelper.floor(start.y);
		int j1 = MathHelper.floor(start.z);

		int k1 = 200;

		while (k1-- >= 0) {
			if (l == i && i1 == j && j1 == k) {
				return returnLastUncollidableBlock ? raytraceresult2 : null;
			}

			boolean flag2 = true;
			boolean flag = true;
			boolean flag1 = true;
			double d0 = 999.0D;
			double d1 = 999.0D;
			double d2 = 999.0D;

			if (i > l) {
				d0 = (double) l + 1.0D;
			} else if (i < l) {
				d0 = (double) l + 0.0D;
			} else {
				flag2 = false;
			}

			if (j > i1) {
				d1 = (double) i1 + 1.0D;
			} else if (j < i1) {
				d1 = (double) i1 + 0.0D;
			} else {
				flag = false;
			}

			if (k > j1) {
				d2 = (double) j1 + 1.0D;
			} else if (k < j1) {
				d2 = (double) j1 + 0.0D;
			} else {
				flag1 = false;
			}

			double d3 = 999.0D;
			double d4 = 999.0D;
			double d5 = 999.0D;
			double d6 = end.x - start.x;
			double d7 = end.y - start.y;
			double d8 = end.z - start.z;

			if (flag2) {
				d3 = (d0 - start.x) / d6;
			}

			if (flag) {
				d4 = (d1 - start.y) / d7;
			}

			if (flag1) {
				d5 = (d2 - start.z) / d8;
			}

			if (d3 == -0.0D) {
				d3 = -1.0E-4D;
			}

			if (d4 == -0.0D) {
				d4 = -1.0E-4D;
			}

			if (d5 == -0.0D) {
				d5 = -1.0E-4D;
			}

			EnumFacing enumfacing;

			if (d3 < d4 && d3 < d5) {
				enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
				start = new Vec3d(d0, start.y + d7 * d3, start.z + d8 * d3);
			} else if (d4 < d5) {
				enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
				start = new Vec3d(start.x + d6 * d4, d1, start.z + d8 * d4);
			} else {
				enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
				start = new Vec3d(start.x + d6 * d5, start.y + d7 * d5, d2);
			}

			l = MathHelper.floor(start.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
			i1 = MathHelper.floor(start.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
			j1 = MathHelper.floor(start.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);

			BlockPos targetPos = new BlockPos(l, i1, j1);
			IBlockState targetState = world.getBlockState(targetPos);
			Block targetBlock = targetState.getBlock();

			if (!isOrigin(targetPos)) {
				if (!ignoreBlocksWithoutBoundingBoxes || targetState.getMaterial() == Material.PORTAL || targetState.getCollisionBoundingBox(world, targetPos) != Block.NULL_AABB) {
					if (targetBlock.canCollideCheck(targetState, false) && (predicateBlock == null || predicateBlock.test(targetBlock))) {
						RayTraceResult raytraceresult1 = targetState.collisionRayTrace(world, targetPos, start, end);

						if (raytraceresult1 != null) return raytraceresult1;

					} else {
						raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, start, enumfacing, targetPos);
					}
				}
			}
		}

		return returnLastUncollidableBlock ? raytraceresult2 : null;
	}

	public RayTrace setSkipBlocks(boolean skipBlocks) {
		this.skipBlocks = skipBlocks;
		return this;
	}
}