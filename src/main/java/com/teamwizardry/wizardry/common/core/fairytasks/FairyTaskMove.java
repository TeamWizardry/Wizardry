package com.teamwizardry.wizardry.common.core.fairytasks;

import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairySequence;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairySequenceBuilder;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTask;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class FairyTaskMove extends FairyTask {

	private static final FairySequence sequence = new FairySequenceBuilder()
			.run(fairy -> {
				if (fairy.isMoving()) return false;

				EntityFairy attached = getChainedFairy(fairy);
				if (attached != null && attached.fairyTaskController.getTask().getPriority() < fairy.fairyTaskController.getTask().getPriority()) {
					Vec3d look = fairy.getLookVec();
					Vec3d targetPos = fairy.getPositionVector().add(look);
					attached.setPosition(targetPos.x, targetPos.y, targetPos.z);
				}

				if (fairy.targetPos == null) return false;

				fairy.moveTo(fairy.targetPos);

				return true;
			})
			.wait(5)
			.run(fairy -> {
				EntityFairy chained = getChainedFairy(fairy);
				if (chained != null) {
					chained.fairyTaskController.getTask().onForceTrigger(chained);
				}

				return true;
			})
			.wait(5)
			.run(fairy -> {
				if (fairy.isMoving()) return false;

				EntityFairy attached = getChainedFairy(fairy);
				if (attached != null && attached.fairyTaskController.getTask().getPriority() < fairy.fairyTaskController.getTask().getPriority()) {
					Vec3d look = fairy.getLookVec();
					Vec3d targetPos = fairy.getPositionVector().add(look);
					attached.setPosition(targetPos.x, targetPos.y, targetPos.z);
				}

				if (fairy.originPos == null) return false;

				fairy.moveTo(fairy.originPos);

				return true;
			})
			.wait(10)
			.build();

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public void onStart(EntityFairy fairy) {

	}

	@Override
	public void onTick(EntityFairy fairy) {
		sequence.tick(fairy);
	}

	@Override
	public void onForceTrigger(EntityFairy fairy) {
	}

	@Override
	public void onEnd(EntityFairy fairy) {

	}

	@Override
	public void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec) {

	}
}
