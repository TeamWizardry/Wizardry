package com.teamwizardry.wizardry.common.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIEntityHurtByTarget extends EntityAITarget {
	private EntityLivingBase owner;
	private EntityLivingBase attacker;
	private int timestamp;

	public EntityAIEntityHurtByTarget(EntityCreature theDefendingTameableIn, EntityLivingBase owner) {
		super(theDefendingTameableIn, false);
		this.owner = owner;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = owner;

		if (entitylivingbase == null || !entitylivingbase.getUniqueID().equals(owner.getUniqueID())) {
			return false;
		} else {
			this.attacker = entitylivingbase.getRevengeTarget();
			int i = entitylivingbase.getRevengeTimer();
			return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.attacker);
		EntityLivingBase entitylivingbase = owner;

		if (entitylivingbase != null) {
			this.timestamp = entitylivingbase.getRevengeTimer();
		}

		super.startExecuting();
	}
}
