package com.teamwizardry.wizardry.api.task;

import com.teamwizardry.wizardry.api.StringConsts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This controls the current active task that an entity has currently.
 * Make a new instance of this in your entity class. Every entity that needs a task controller will get its own instance.
 * Use this controller to change and control the task an entity must follow.
 * <p>
 * Will hold a queue of all tasks that need to be done.
 * <p>
 * Be sure to save the controller in the entity's nbt.
 */
public class TaskController implements INBTSerializable<CompoundNBT> {

	private final Queue<Task> referenceQueue = new LinkedList<>();

	private final Queue<Task> activeQueue = new LinkedList<>();

	private TaskStorage storage = new TaskStorage();

	public TaskController() {
	}

	public TaskStorage getStorage() {
		return storage;
	}

	@Nonnull
	public static TaskController deserialize(CompoundNBT nbt) {
		TaskController controller = new TaskController();
		controller.deserializeNBT(nbt);
		return controller;
	}

	public <R extends Entity & IRobot> void tick(R entity) {
		if (entity instanceof LivingEntity && !entity.isAlive()) return;

		Task peek = activeQueue.peek();
		if (peek == null) return;

		peek.onTick(entity, this);
	}

	public void setTaskQueue(Task... tasks) {
		referenceQueue.clear();
		referenceQueue.addAll(Arrays.asList(tasks));
	}

	public void resetQueue() {
		activeQueue.clear();
		activeQueue.addAll(referenceQueue);
	}

	public <R extends Entity & IRobot> void next(R entity) {
		if (entity instanceof LivingEntity && !entity.isAlive()) return;

		Task poll = activeQueue.poll();
		if (poll == null) {
			if (referenceQueue.isEmpty()) return;

			resetQueue();

			Task peek = activeQueue.peek();
			if (peek == null) return;
			peek.onStart(entity, this);

			return;
		}
		poll.onEnd(entity, this);

		Task nextTask = activeQueue.peek();
		if (nextTask == null) {
			if (referenceQueue.isEmpty()) return;

			resetQueue();

			Task peek = activeQueue.peek();
			if (peek == null) return;
			peek.onStart(entity, this);

			return;
		}
		nextTask.onStart(entity, this);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();

		Queue<Task> queueCopy = new LinkedList<>(referenceQueue);

		ListNBT listNBT = new ListNBT();
		while (!queueCopy.isEmpty()) {
			Task task = queueCopy.poll();
			if (task == null) continue;

			CompoundNBT taskNBT = new CompoundNBT();
			//TODO learn how to get resourceloc from forgeregisteries
			//taskNBT.putString(StringConsts.RESOURCE_LOCATION, ForgeRegistries.DATA_SERIALIZERS.getKey()task.getResourceLocation().toString());
			taskNBT.put(StringConsts.DATA, task.serializeNBT());

			listNBT.add(taskNBT);
		}

		nbt.put(StringConsts.QUEUE, listNBT);

		nbt.put(StringConsts.TASK_STORAGE, storage.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

		if (nbt.contains(StringConsts.QUEUE)) {
			ListNBT listNBT = nbt.getList(StringConsts.QUEUE, NBT.TAG_COMPOUND);
			for (INBT inbt : listNBT) {
				if (!(inbt instanceof CompoundNBT)) continue;

				CompoundNBT taskNBT = (CompoundNBT) inbt;

				if (taskNBT.contains(StringConsts.RESOURCE_LOCATION) && taskNBT.contains(StringConsts.DATA)) {
					ResourceLocation resourceLocation = new ResourceLocation(taskNBT.getString(StringConsts.RESOURCE_LOCATION));
					CompoundNBT data = taskNBT.getCompound(StringConsts.DATA);

					//Task freshTask = TaskRegistry.supplyTask(resourceLocation);
					//freshTask.deserializeNBT(data);
					//this.referenceQueue.add(freshTask);
				}
			}
		}

		if (nbt.contains(StringConsts.TASK_STORAGE)) {
			this.storage = new TaskStorage();
			this.storage.deserializeNBT(nbt.getCompound(StringConsts.TASK_STORAGE));
		}
	}
}
