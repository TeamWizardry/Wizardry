package com.teamwizardry.wizardry.common.entity.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class EntityAITargetFiltered<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
	@SuppressWarnings("unused")
	private final EntityLivingBase entity;

	public EntityAITargetFiltered(EntityLivingBase entityIn, Class<T> classTarget, boolean checkSight, Predicate<? super T> targetSelector) {
		super((EntityCreature) entityIn, classTarget, 10, checkSight, false, targetSelector);
		this.entity = entityIn;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		return super.shouldExecute();
	}
}
