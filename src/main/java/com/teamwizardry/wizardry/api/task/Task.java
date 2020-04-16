package com.teamwizardry.wizardry.api.task;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class Task {

	@Nullable
	protected static Entity getChainedRobot(Entity robotEntity) {
		if (!(robotEntity instanceof IRobot)) return null;

		IRobot robot = (IRobot) robotEntity;

		UUID attachedUUID = robot.getTaskStorage().chainedTo;
		if (attachedUUID == null) return null;

		ServerWorld world = (ServerWorld) robotEntity.world;

		return world.getEntityByUuid(attachedUUID);
	}

	/**
	 * When the robot accepts the task, this runs
	 */
	public abstract void onStart(Entity robotEntity);

	/**
	 * Will tick every tick the robot entity ticks with this task.
	 */
	public abstract void onTick(Entity robotEntity);

	/**
	 * Will run when the robot accepts a different task.
	 */
	public abstract void onEnd(Entity robotEntity);

	/**
	 * Can be called at any time by anything. Usually by the fairy chained to this one, or a timer of some sort.
	 */
	public abstract void onTrigger(Entity robot);

	/**
	 * You may add additional processing to the robot whenever it is reconfigured with a bell.
	 *
	 * @param robotEntity The robot entity.
	 * @param targetBlock The target block configured to. Can be null (target entity instead).
	 * @param lookVec     The target look vector.
	 */
	public abstract void onConfigure(Entity robotEntity, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec);

}
