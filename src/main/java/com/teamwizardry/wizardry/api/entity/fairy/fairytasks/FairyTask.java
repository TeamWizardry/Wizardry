package com.teamwizardry.wizardry.api.entity.fairy.fairytasks;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class FairyTask {

	/**
	 * The higher, the more priority this task will take.
	 */
	public abstract int getPriority();

	/**
	 * When the fairy accepts the task, this runs
	 */
	public abstract void onStart(EntityFairy fairy);

	/**
	 * Will tick every tick the fairy ticks with this task.
	 */
	public abstract void onTick(EntityFairy fairy);

	/**
	 * Will run when the fairy accepts a different task.
	 */
	public abstract void onEnd(EntityFairy fairy);

	@Nullable
	protected static EntityFairy getChainedFairy(EntityFairy fairy) {
		UUID attachedUUID = fairy.getChainedFairy();

		List<Entity> list = fairy.world.loadedEntityList;
		for (Entity entity : list) {
			if (entity instanceof EntityFairy && entity.getUniqueID().equals(attachedUUID)) {
				if (entity.isDead) continue;

				return (EntityFairy) entity;
			}
		}

		return null;
	}

	protected static boolean isPriorityTaken(EntityFairy fairy) {
		UUID attachedUUID = fairy.getChainedFairy();

		List<Entity> list = fairy.world.loadedEntityList;
		for (Entity entity : list) {
			if (entity instanceof EntityFairy && entity.getUniqueID().equals(attachedUUID)) {
				if (entity.isDead) continue;

				EntityFairy attachedFairy = (EntityFairy) entity;
				return attachedFairy.fairyTaskController.getTask().getPriority() > fairy.fairyTaskController.getTask().getPriority();
			}
		}

		return false;
	}

	public abstract void onForceTrigger(EntityFairy fairy);

	/**
	 * You may add additional processing to the fairy whenever it is reconfigured with a bell.
	 *
	 * @param fairy       The fairy entity.
	 * @param targetBlock The target block configured to. Can be null (target entity instead).
	 * @param lookVec     The target look vector set to. This can be the same as before, but can also change.
	 */
	public abstract void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec);
}
