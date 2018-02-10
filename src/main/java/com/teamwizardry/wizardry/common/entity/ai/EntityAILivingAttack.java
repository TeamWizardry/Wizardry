package com.teamwizardry.wizardry.common.entity.ai;

import com.teamwizardry.wizardry.common.entity.EntityBackupZombie;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;

public class EntityAILivingAttack extends EntityAIAttackMelee {
	private final EntityLivingBase entity;
	private int raiseArmTicks;

	public EntityAILivingAttack(EntityLivingBase entity, double speedIn, boolean longMemoryIn) {
		super((EntityCreature) entity, speedIn, longMemoryIn);
		this.entity = entity;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting() {
		super.startExecuting();
		this.raiseArmTicks = 0;
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	@Override
	public void resetTask() {
		super.resetTask();
		if (entity instanceof EntityBackupZombie) ((EntityBackupZombie) entity).setArmsRaised(false);
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@Override
	public void updateTask() {
		super.updateTask();
		++this.raiseArmTicks;

		if (this.raiseArmTicks >= 5 && this.attackTick < 10) {
			if (entity instanceof EntityBackupZombie) ((EntityBackupZombie) entity).setArmsRaised(true);
		} else {
			if (entity instanceof EntityBackupZombie) ((EntityBackupZombie) entity).setArmsRaised(false);
		}
	}
}
