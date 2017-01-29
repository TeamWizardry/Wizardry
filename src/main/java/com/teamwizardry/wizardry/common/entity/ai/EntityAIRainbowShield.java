package com.teamwizardry.wizardry.common.entity.ai;

import com.teamwizardry.wizardry.common.entity.EntityUnicorn;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.Vec3d;

public class EntityAIRainbowShield extends EntityAIBase {
    //At what percentage of the distance between an attacker and the unicorn the shield should spawn.
    private static final float DIST_MULT = 0.5F;

    private EntityUnicorn attacker;
    private float minRange;

    public EntityAIRainbowShield(EntityUnicorn attacker, float minRange) {
	this.attacker = attacker;
	this.minRange = minRange;
    }

    @Override
    public boolean shouldExecute() {
	EntityLivingBase target = this.attacker.getAttackTarget();
	if(target == null || !target.isEntityAlive()) return false;
	if(target.getDistanceToEntity(attacker) <= minRange) return false;
	if(attacker.getShieldCooldown() > 0) return false;
	return true;
    }

    @Override
    public void startExecuting() {
	EntityLivingBase target = this.attacker.getAttackTarget(); 
	Vec3d shieldPos = attacker.getPositionVector().add(target.getPositionVector().subtract(attacker.getPositionVector()).scale(DIST_MULT));
	
	attacker.resetShieldCooldown();
    }

    public boolean continueExecuting() {return false;}

    public void resetTask() {
	this.attacker.getNavigator().clearPathEntity();
    }
}
