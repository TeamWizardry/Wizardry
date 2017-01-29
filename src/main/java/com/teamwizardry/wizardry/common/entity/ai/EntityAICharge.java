package com.teamwizardry.wizardry.common.entity.ai;

import com.teamwizardry.wizardry.common.entity.EntityUnicorn;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAICharge extends EntityAIBase {
    
	private EntityUnicorn attacker;
	private float speed;
	private float maxRange;
	private boolean targetAttacked;
	private BlockPos chargeStartPos = BlockPos.ORIGIN;

	public EntityAICharge(EntityUnicorn attacker, float speed, float maxRange) {
		this.attacker = attacker;
		this.speed = speed;
		this.maxRange = maxRange;
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase target = this.attacker.getAttackTarget();
		if(target == null || !target.isEntityAlive()) return false;
		if(target.getDistanceToEntity(attacker) > maxRange) return false;
		if(attacker.getChargeCooldown() > 0) return false;
		return true;
	}
	
	@Override
	public void startExecuting() {
		this.attacker.getNavigator().setPath(getChargePath(this.attacker.getAttackTarget()), speed);
		chargeStartPos = this.attacker.getPosition();
		attacker.resetChargeCooldown();
	}
	
	@Override
	public boolean continueExecuting() {
		EntityLivingBase target = this.attacker.getAttackTarget();
		if(target == null || !target.isEntityAlive() || attacker.getDistanceToEntity(target) >= 16.0D) {
			return false;
		}
		if(attacker.getNavigator().noPath()) {
			attacker.setAttackTarget(null);
			return false;
		}
		if(target instanceof EntityPlayer 
			&& (((EntityPlayer) target).capabilities.isCreativeMode || ((EntityPlayer) target).isSpectator())) {
			return false;
		}
		return true;
	}
	
	@Override
	public void updateTask() {
		if(this.attacker.getEntityBoundingBox().intersectsWith(this.attacker.getAttackTarget().getEntityBoundingBox()))
		{
			this.attacker.getAttackTarget().knockBack(attacker, 0.6F, MathHelper.sin(this.attacker.rotationYaw), -MathHelper.cos(this.attacker.rotationYaw));
			this.attacker.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(attacker), (float) attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
			targetAttacked = true;
		}
		if(targetAttacked)
		{
			this.attacker.getNavigator().tryMoveToXYZ(chargeStartPos.getX(), chargeStartPos.getY(), chargeStartPos.getZ(), speed);
		}
	}
	
	public void resetTask() {
		 this.attacker.getNavigator().clearPathEntity();
		 this.targetAttacked = false;
	 }
	
	public Path getChargePath(EntityLivingBase target)
	{
		return new Path(new PathPoint[] {new PathPoint(this.attacker.getPosition().getX(), this.attacker.getPosition().getY(), this.attacker.getPosition().getZ())
				, new PathPoint(target.getPosition().getX(), target.getPosition().getY(), target.getPosition().getZ())});
	}
}
