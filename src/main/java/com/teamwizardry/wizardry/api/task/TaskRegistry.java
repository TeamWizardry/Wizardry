package com.teamwizardry.wizardry.api.task;

import com.teamwizardry.wizardry.api.ResourceConsts;
import com.teamwizardry.wizardry.api.task.defaulttasks.TaskIdle;
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
		registerTask(new TaskIdle(), TaskIdle::new);
	}

	private TaskRegistry() {
	}

	/**
	 * You first pass the task, then a supplier of that exact same task. This is to prevent user error and we always
	 * have the same resourcelocation without you needing to write it again outside the Task itself.
	 *
	 * @param task     Contains a resourcelocation containing the mod id as a prefix, and an identifier string for your task.
	 * @param supplier Contains a supplier for the same task so we can generate a fresh one and deserialize it later so it persists.
	 * @return True if registration succeeded, false if a task with the same resource location is already registered.
	 */
	public static boolean registerTask(@Nonnull Task task, Supplier<Task> supplier) {
		if (tasks.containsKey(task.getResourceLocation())) {
			return false;
		}

		tasks.put(task.getResourceLocation(), supplier);
		return true;
	}

	@Nonnull
	public static Task supplyTask(@Nonnull ResourceLocation resourceLocation) {
		if (tasks.containsKey(resourceLocation)) return tasks.get(resourceLocation).get();

		return tasks.get(ResourceConsts.TASK_IDLE).get();
	}

}
