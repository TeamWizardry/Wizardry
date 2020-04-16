package com.teamwizardry.wizardry.api.task;

import com.teamwizardry.wizardry.api.ResourceConsts;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Register a new task with this class. Tasks are used to control entities in a queue-like fashion.
 * This is used for fairy slaving.
 */
public class TaskRegistry {

	private static final HashMap<ResourceLocation, Supplier<Task>> tasks = new HashMap<>();

	static {
		registerTask(TaskWait::new);
	}

	private TaskRegistry() {
	}

	/**
	 * Registering your task enables us to serialize and deserialize it. Your task will NOT save if you don't register it!
	 *
	 * @param supplier Contains a supplier for the task so we can generate a fresh one and deserialize it later for persistence.
	 * @return True if registration succeeded, false if a task with the same resource location is already registered.
	 */
	public static boolean registerTask(@Nonnull Supplier<Task> supplier) {
		Task referenceTask = supplier.get();
		if (tasks.containsKey(referenceTask.getResourceLocation())) {
			return false;
		}

		tasks.put(referenceTask.getResourceLocation(), supplier);
		return true;
	}

	@Nonnull
	public static Task supplyTask(@Nonnull ResourceLocation resourceLocation) {
		if (tasks.containsKey(resourceLocation)) return tasks.get(resourceLocation).get();

		return tasks.get(ResourceConsts.TASK_IDLE).get();
	}

}
