package com.teamwizardry.wizardry.common.entity.ai;

import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWanderAvoidWaterFlying;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class FairyAIWanderAvoidWaterFlying extends EntityAIWanderAvoidWaterFlying {

	public FairyAIWanderAvoidWaterFlying(EntityCreature p_i47413_1_, double p_i47413_2_) {
		super(p_i47413_1_, p_i47413_2_);
	}

	@Override
	public boolean shouldExecute() {
		if (!this.mustUpdate) {
			if (this.entity.getIdleTime() >= 100) {
				return false;
			}

			if (this.entity.getRNG().nextInt(3) != 0) {
				return false;
			}
		}

		Vec3d vec3d = this.getPosition();

		if (vec3d == null) {
			return false;
		} else {
			this.x = vec3d.x;
			this.y = vec3d.y;
			this.z = vec3d.z;
			this.mustUpdate = false;
			return true;
		}
	}

	@Nullable
	@Override
	protected Vec3d getPosition() {
		Vec3d vec3d;

		if (this.entity.getRNG().nextFloat() == 0.1) {
			vec3d = RandomPositionGenerator.getLandPos(this.entity, 16, 16);
		} else {
			vec3d = entity.getPositionVector().add(RandUtil.nextDouble(-32, 32), RandUtil.nextDouble(-32, 32), RandUtil.nextDouble(-32, 32));
			vec3d = new Vec3d(vec3d.x, (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed()) ? vec3d.y : MathHelper.clamp(vec3d.y, 0, 255), vec3d.z);
		}

		return vec3d == null ? super.getPosition() : vec3d;
	}
}
