package com.teamwizardry.wizardry.common.core.fairytasks;

import com.teamwizardry.wizardry.api.StateGraph;
import com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTask;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class FairyTaskMove extends FairyTask {

	private final StateGraph<EntityFairy> graph = new StateGraph.Builder<EntityFairy>()
			.runWhile(fairy -> true, entityFairyBuilder -> entityFairyBuilder

					.run(fairy -> {
						if (fairy.isMoving()) return false;
						if (fairy.targetPos == null) return false;

						fairy.moveTo(fairy.targetPos);

						return true;
					})

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
						if (fairy.originPos == null) return false;

						fairy.moveTo(fairy.originPos);

						return true;
					})
					.wait(10))
			.build();

	private final StateGraph<EntityFairy> draggerGraph = new StateGraph.Builder<EntityFairy>()
			.runWhile(fairy -> true, entityFairyBuilder -> entityFairyBuilder.run(fairy -> {
				EntityFairy attached = getChainedFairy(fairy);
				if (attached != null && attached.fairyTaskController.getTask().getPriority() < fairy.fairyTaskController.getTask().getPriority()) {
					Vec3d look = fairy.getLookVec();
					Vec3d targetPos = fairy.getPositionVector().add(look);
					attached.setPositionAndUpdate(targetPos.x, targetPos.y, targetPos.z);
				}
				return true;
			}).wait(1))
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
		graph.offer(fairy);
		draggerGraph.offer(fairy);
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
