package com.teamwizardry.wizardry.api.util;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EntityTrace {

	@NotNull
	public World world;
	@NotNull
	public Vec3d pos;
	@NotNull
	public Vec3d slope;
	public double range;
	public boolean ignoreEntities;
	@Nullable
	public UUID uuidToSkip;

	/**
	 * May not exist; used only for the recasting utilities in Beam.
	 */
	@Nullable
	public RayTraceResult rayTraceResult;

	public EntityTrace(@NotNull World world, @NotNull Vec3d pos, @NotNull Vec3d slope, double range) {
		this.world = world;
		this.pos = pos;
		this.slope = slope;
		this.range = range;
	}

	private static RayTraceResult blockTrace(World world, Vec3d pos, Vec3d ray, double distance) {
		Vec3d cast = pos.add(ray.normalize().scale(distance));
		return BlockTrace.rayTraceBlocks(world, new HashSet<>(ImmutableList.of(new BlockPos(pos))), pos, cast, false, false, true);
	}

	public EntityTrace setRange(double range) {
		this.range = range;
		return this;
	}

	public EntityTrace setIgnoreEntities(boolean ignoreEntities) {
		this.ignoreEntities = ignoreEntities;
		return this;
	}

	public EntityTrace setUUIDToSkip(UUID uuidToSkip) {
		this.uuidToSkip = uuidToSkip;
		return this;
	}

	/**
	 * Will create a raycastResult from the defined fields in the class.
	 *
	 * @return The raycast result from all the defined fields in the class.
	 */
	public RayTraceResult cast() {
		RayTraceResult focusedBlock = blockTrace(world, pos, slope, range);
		double blockDistance = range;

		if (focusedBlock != null) blockDistance = focusedBlock.hitVec.distanceTo(pos);

		Vec3d cast = pos.addVector(slope.xCoord * range, slope.yCoord * range, slope.zCoord * range);
		Entity focusedEntity = null;
		Vec3d vec = null;
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(new BlockPos(pos)).addCoord(slope.xCoord * range, slope.yCoord * range, slope.zCoord * range).expand(1, 1, 1), Predicates.and(apply -> apply != null && !ignoreEntities && (apply.canBeCollidedWith() || (apply instanceof EntityItem)), EntitySelectors.NOT_SPECTATING));
		double blockDistCopy = blockDistance;

		int j = 0;
		while (j < list.size()) {
			Entity current = list.get(j);
			AxisAlignedBB axis = current.getEntityBoundingBox().expandXyz(current.getCollisionBorderSize());

			if (uuidToSkip != null && current.getUniqueID().equals(uuidToSkip)) {
				j++;
				continue;
			}

			if ((current instanceof EntityLivingBase &&
					((EntityLivingBase) current).getActivePotionEffect(MobEffects.INVISIBILITY) != null) ||
					(current instanceof EntityPlayer && ((EntityPlayer) current).isSpectator())) {
				j++;
				continue;
			}

			if (current instanceof EntityItem)
				axis = axis.expand(0, 0.25, 0);
			RayTraceResult result = axis.calculateIntercept(pos, cast);

			if (axis.isVecInside(pos)) {
				if (blockDistCopy > 0) {
					focusedEntity = current;
					vec = result == null ? pos : result.hitVec;
					blockDistCopy = 0;
				}
			} else if (result != null) {
				double entityDistance = pos.distanceTo(result.hitVec);

				if (entityDistance < blockDistCopy || blockDistCopy == 0) {
					focusedEntity = current;
					vec = result.hitVec;
					blockDistCopy = entityDistance;
				}
			}

			if (focusedEntity != null && (blockDistCopy < blockDistance || focusedBlock == null)) {
				focusedBlock = new RayTraceResult(focusedEntity, vec);
				if (focusedEntity instanceof EntityLivingBase || focusedEntity instanceof EntityItem) {
					return focusedBlock;
				}
			}
			j++;
		}
		return focusedBlock;
	}
}
