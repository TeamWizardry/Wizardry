package com.teamwizardry.wizardry.common.entity.ai;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.List;

public class EntityAIUnicornAttackNearestAttackableTarget<T extends EntityLivingBase> extends EntityAIUnicornTarget {

	protected final Class<T> targetClass;
	protected final EntityAINearestAttackableTarget.Sorter sorter;
	protected final Predicate<? super T> targetEntitySelector;
	private final int targetChance;
	protected T targetEntity;

	public EntityAIUnicornAttackNearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight) {
		this(creature, classTarget, checkSight, false);
	}

	public EntityAIUnicornAttackNearestAttackableTarget(EntityCreature creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
		this(creature, classTarget, 10, checkSight, onlyNearby);
	}

	public EntityAIUnicornAttackNearestAttackableTarget(EntityCreature creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby) {
		super(creature, checkSight, onlyNearby);
		this.targetClass = classTarget;
		this.targetChance = chance;
		this.sorter = new EntityAINearestAttackableTarget.Sorter(creature);
		this.setMutexBits(1);
		this.targetEntitySelector = (Predicate<T>) entity -> EntitySelectors.NOT_SPECTATING.apply(entity) && isSuitableTarget(entity, false);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
			return false;
		} else if (this.targetClass != EntityPlayer.class && this.targetClass != EntityPlayerMP.class) {
			List<T> list = this.taskOwner.world.getEntitiesWithinAABB(this.targetClass, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);

			if (list.isEmpty()) {
				return false;
			} else {
				list.sort(this.sorter);
				this.targetEntity = list.get(0);
				return true;
			}
		} else {
			this.targetEntity = (T) this.taskOwner.world.getNearestAttackablePlayer(this.taskOwner.posX, this.taskOwner.posY + (double) this.taskOwner.getEyeHeight(), this.taskOwner.posZ, this.getTargetDistance(), this.getTargetDistance(), new Function<EntityPlayer, Double>() {
				@Nullable
				public Double apply(@Nullable EntityPlayer player) {
					return 1.0D;
				}
			}, (Predicate<EntityPlayer>) this.targetEntitySelector);
			return this.targetEntity != null;
		}
	}

	protected AxisAlignedBB getTargetableArea(double targetDistance) {
		return this.taskOwner.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.targetEntity);
		super.startExecuting();
	}


}