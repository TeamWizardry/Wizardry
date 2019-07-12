package com.teamwizardry.wizardry.common.core.fairytasks;

import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTask;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class FairyTaskMove extends FairyTask {

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public void onStart(EntityFairy fairy) {

	}

	@Override
	public void onTick(EntityFairy fairy) {
		if (fairy.isMoving()) {
			EntityFairy attached = getAttachedFairy(fairy);
			if (attached != null && attached.fairyTaskController.getTask().getPriority() < getPriority()) {
				Vec3d look = fairy.getLookVec();
				Vec3d targetPos = fairy.getPositionVector().add(look);
				attached.setPosition(targetPos.x, targetPos.y, targetPos.z);
			}
			return;
		}

		if (fairy.targetPos == null || fairy.originPos == null) return;

		double fairyToTarget = fairy.getPositionVector().distanceTo(new Vec3d(fairy.targetPos).add(0.5, 0.5, 0.5));
		double fairyToOrigin = fairy.getPositionVector().distanceTo(new Vec3d(fairy.originPos).add(0.5, 0.5, 0.5));

		if (fairyToTarget > 0.25) {
			fairy.moveTo(fairy.targetPos);
		} else if (fairyToOrigin > 0.25)
			fairy.moveTo(fairy.originPos);
	}

	@Override
	public void onEnd(EntityFairy fairy) {

	}

	@Override
	public void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec) {

	}
}
