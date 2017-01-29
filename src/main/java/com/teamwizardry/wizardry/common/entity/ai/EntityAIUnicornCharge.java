package com.teamwizardry.wizardry.common.entity.ai;

import com.teamwizardry.wizardry.common.entity.EntityUnicorn;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

public class EntityAIUnicornCharge extends EntityAIBase {

	private EntityUnicorn unicorn;
	private float speed;
	private float maxRange;
	private double damage;
	private boolean targetHit = false;

	public EntityAIUnicornCharge(EntityUnicorn unicorn, float speed, float maxRange, double damage) {
		this.unicorn = unicorn;
		this.speed = speed;
		this.maxRange = maxRange;
		this.damage = damage;
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase target = this.unicorn.getAttackTarget();
		if (target == null || !target.isEntityAlive()) return false;
		return !(target.getDistanceToEntity(unicorn) > maxRange);
	}

	@Override
	public void startExecuting() {
		if (unicorn.getAttackTarget() == null) return;
		unicorn.isCharging = true;
		unicorn.prepareChargeTicks = 0;
	}

	@Override
	public boolean continueExecuting() {
		EntityLivingBase target = this.unicorn.getAttackTarget();
		if (target == null || !target.isEntityAlive() || unicorn.getDistanceToEntity(target) >= maxRange * 1.5)
			return false;

		if (target instanceof EntityPlayer && (((EntityPlayer) target).capabilities.isCreativeMode || ((EntityPlayer) target).isSpectator()))
			return false;

		return !targetHit;
	}

	@Override
	public void updateTask() {
		if (unicorn.getAttackTarget() == null) return;

		Minecraft.getMinecraft().player.sendChatMessage(unicorn.prepareChargeTicks + " - " + unicorn.isCharging);
		if (unicorn.isCharging && unicorn.prepareChargeTicks < 40) {
			unicorn.limbSwingAmount += 0.6F;
			unicorn.prepareChargeTicks++;
			return;
		} else unicorn.getNavigator().tryMoveToEntityLiving(unicorn.getAttackTarget(), speed);

		if (unicorn.getEntityBoundingBox().expand(1, 1, 1).intersectsWith(unicorn.getAttackTarget().getEntityBoundingBox())) {
			unicorn.getAttackTarget().knockBack(unicorn, 3F, MathHelper.sin(this.unicorn.rotationYaw), -MathHelper.cos(this.unicorn.rotationYaw));
			unicorn.knockBack(unicorn, 0.5F, -MathHelper.sin(this.unicorn.rotationYaw), MathHelper.cos(this.unicorn.rotationYaw));
			unicorn.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(unicorn), (float) damage);
			targetHit = true;
		}
	}

	public void resetTask() {
		this.unicorn.getNavigator().clearPathEntity();
		unicorn.isCharging = false;
		unicorn.prepareChargeTicks = 0;
	}
}
