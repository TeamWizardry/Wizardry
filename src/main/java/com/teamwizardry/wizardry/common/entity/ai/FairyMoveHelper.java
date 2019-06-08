package com.teamwizardry.wizardry.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityFlyHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.MathHelper;

public class FairyMoveHelper extends EntityFlyHelper {

	public FairyMoveHelper(EntityLiving p_i47418_1_) {
		super(p_i47418_1_);
	}

	@Override
	public void read(EntityMoveHelper that) {
		super.read(that);
		this.speed = that.getSpeed();
	}

	@Override
	public void onUpdateMoveHelper() {
		if (this.action == EntityMoveHelper.Action.MOVE_TO) {
			double x = this.posX - this.entity.posX;
			double y = this.posY - this.entity.posY;
			double z = this.posZ - this.entity.posZ;
			double distance = x * x + y * y + z * z;
			distance = (double) MathHelper.sqrt(distance);

			if (distance < this.entity.getEntityBoundingBox().getAverageEdgeLength()) {
				this.action = EntityMoveHelper.Action.WAIT;
				//	this.entity.motionX *= 0.9D;
				//	this.entity.motionY *= 0.9D;
				//	this.entity.motionZ *= 0.9D;
			} else {
				this.entity.motionX += x / distance * 0.05D * this.speed;
				this.entity.motionY += y / distance * 0.05D * this.speed;
				this.entity.motionZ += z / distance * 0.05D * this.speed;

				if (this.entity.getAttackTarget() == null) {
					this.entity.rotationYaw = -((float) MathHelper.atan2(this.entity.motionX, this.entity.motionZ)) * (180F / (float) Math.PI);
					this.entity.renderYawOffset = this.entity.rotationYaw;
				} else {
					double d4 = this.entity.getAttackTarget().posX - this.entity.posX;
					double d5 = this.entity.getAttackTarget().posZ - this.entity.posZ;
					this.entity.rotationYaw = -((float) MathHelper.atan2(d4, d5)) * (180F / (float) Math.PI);
					this.entity.renderYawOffset = this.entity.rotationYaw;
				}
			}
		}
	}
}
