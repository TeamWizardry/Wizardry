package com.teamwizardry.wizardry.api.task;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This controls the current active task that an entity has currently.
 * Make a new instance of this in your entity class. Every entity that needs a task controller will get its own instance.
 * Use this controller to change and control the task an entity must follow.
 * Be sure to save the controller in the entity's nbt.
 */
public class TaskController implements INBTSerializable<CompoundNBT> {

	private static final String TASK_LOCATION = "task_location";

	@Nonnull
	private ResourceLocation taskLocation = TaskRegistry.IDLE_TASK;

	@Nonnull
	private Task task = TaskRegistry.getTask(TaskRegistry.IDLE_TASK);

	public TaskController() {
	}

	@Nonnull
	public static TaskController deserialize(CompoundNBT nbt) {
		TaskController controller = new TaskController();
		controller.deserializeNBT(nbt);
		return controller;
	}

	public void tick(Entity entity) {
		task.onTick(entity);
	}

	@Nonnull
	public Task getTask() {
		return task;
	}

	/**
	 * Changes the current task. Will end the last one and start the new one.
	 *
	 * @param entity   The entity is nullable for serialization. When deserialization happens, we don't want it to trigger onStart and onEnd again.
	 * @param location The new task's resource location. Must be a valid task location that's registered in TaskRegistry
	 */
	public void setCurrentTask(@Nullable Entity entity, @Nonnull ResourceLocation location) {
		task.onEnd(entity);
		taskLocation = location;
		task = TaskRegistry.getTask(location);
		task.onStart(entity);
	}

	public void setTaskToIdle(Entity entity) {
		task.onEnd(entity);
		taskLocation = TaskRegistry.IDLE_TASK;
		task = TaskRegistry.getTask(TaskRegistry.IDLE_TASK);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString(TASK_LOCATION, taskLocation.toString());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains(TASK_LOCATION))
			setCurrentTask(null, new ResourceLocation(nbt.getString(TASK_LOCATION)));
	}
}
