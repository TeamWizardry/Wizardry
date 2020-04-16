package com.teamwizardry.wizardry.api.task;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * TASKS MUST ALWAYS IMPLEMENT AN EMPTY CONSTRUCTOR FOR REGISTRATION AND SERIALIZATION
 */
public abstract class Task implements INBTSerializable<CompoundNBT> {

	@Nullable
	protected static <R extends Entity & IRobot> Entity getChainedRobot(R robotEntity) {

		UUID attachedUUID = robotEntity.getTaskStorage().chainedTo;
		if (attachedUUID == null) return null;

		ServerWorld world = (ServerWorld) robotEntity.world;

		return world.getEntityByUuid(attachedUUID);
	}

	/**
	 * Contains the mod id as a prefix, and an identifier string for your task.
	 */
	public abstract ResourceLocation getResourceLocation();

	/**
	 * When the robot accepts the task, this runs
	 */
	public abstract <R extends Entity & IRobot> void onStart(R robotEntity, TaskController controller);

	/**
	 * Will tick every tick the robot entity ticks with this task.
	 */
	public abstract <R extends Entity & IRobot> void onTick(R robotEntity, TaskController controller);

	/**
	 * Will run when the robot accepts a different task.
	 */
	public abstract <R extends Entity & IRobot> void onEnd(R robotEntity, TaskController controller);

	/**
	 * Can be called at any time by anything. Usually by the fairy chained to this one, or a timer of some sort.
	 */
	public abstract <R extends Entity & IRobot> void onTrigger(R robot, TaskController controller);

	/**
	 * You may add additional processing to the robot whenever it is reconfigured with a bell.
	 *
	 * @param robotEntity The robot entity.
	 * @param targetBlock The target block configured to. Can be null (target entity instead).
	 * @param lookVec     The target look vector.
	 */
	public abstract <R extends Entity & IRobot> void onConfigure(R robotEntity, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec, TaskController controller);

}
