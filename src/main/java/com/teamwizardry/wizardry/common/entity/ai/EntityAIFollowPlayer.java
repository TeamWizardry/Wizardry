package com.teamwizardry.wizardry.common.entity.ai;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityAIFollowPlayer extends EntityAIBase {

	private final EntityLiving entity;
	private final double followSpeed;
	private final PathNavigate petPathfinder;
	private World world;
	private float maxDist;
	private float minDist;
	@Nullable
	private EntityLivingBase owner;
	private int timeToRecalcPath;
	private float oldWaterCost;

	public EntityAIFollowPlayer(EntityLiving entity, double followSpeedIn, float minDistIn, float maxDistIn) {
		this.entity = entity;
		this.world = entity.world;
		this.followSpeed = followSpeedIn;
		this.petPathfinder = entity.getNavigator();
		this.minDist = minDistIn;
		this.maxDist = maxDistIn;
		this.setMutexBits(3);

		UUID exclude = entity.getEntityData().getUniqueId("owner");
		if (exclude != null) {
			EntityPlayer owner = world.getPlayerEntityByUUID(exclude);
			if (owner != null) {
				this.owner = owner;
			}
		}

		if (!(entity.getNavigator() instanceof PathNavigateGround)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = owner;

		if (entitylivingbase == null) {
			return false;
		} else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator()) {
			return false;
		} else if (this.entity.getDistance(entitylivingbase) < this.minDist) {
			return false;
		} else {
			this.owner = entitylivingbase;
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting() {
		return !this.petPathfinder.noPath() && (this.owner != null && this.entity.getDistance(this.owner) > this.maxDist);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.entity.getPathPriority(PathNodeType.WATER);
		this.entity.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	@Override
	public void resetTask() {
		this.owner = null;
		this.petPathfinder.clearPath();
		this.entity.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	private boolean isEmptyBlock(BlockPos pos) {
		IBlockState iblockstate = this.world.getBlockState(pos);
		return iblockstate.getMaterial() == Material.AIR || !iblockstate.isFullCube();
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void updateTask() {
		if (this.owner == null) return;
		this.entity.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float) this.entity.getVerticalFaceSpeed());

		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 10;

			if (!this.petPathfinder.tryMoveToEntityLiving(this.owner, this.followSpeed)) {
				if (!this.entity.getLeashed()) {
					if (this.entity.getDistance(this.owner) >= 12.0D) {
						int i = MathHelper.floor(this.owner.posX) - 2;
						int j = MathHelper.floor(this.owner.posZ) - 2;
						int k = MathHelper.floor(this.owner.getEntityBoundingBox().minY);

						for (int l = 0; l <= 4; ++l) {
							for (int i1 = 0; i1 <= 4; ++i1) {
								if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.world.getBlockState(new BlockPos(i + l, k - 1, j + i1)).isTopSolid() && this.isEmptyBlock(new BlockPos(i + l, k, j + i1)) && this.isEmptyBlock(new BlockPos(i + l, k + 1, j + i1))) {
									this.entity.setLocationAndAngles((double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), this.entity.rotationYaw, this.entity.rotationPitch);
									this.petPathfinder.clearPath();
									return;
								}
							}
						}
					}
				}
			}
		}
	}
}
