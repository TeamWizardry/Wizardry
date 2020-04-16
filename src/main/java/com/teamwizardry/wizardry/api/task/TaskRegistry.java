package com.teamwizardry.wizardry.api.task;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * Register a new task with this class. Tasks are used to control entities in a queue-like fashion.
 * This is used for fairy slaving.
 */
public class TaskRegistry {

	public static final ResourceLocation IDLE_TASK = new ResourceLocation(Wizardry.MODID, "idle");

	private static final HashMap<ResourceLocation, Task> tasks = new HashMap<>();

	static {
		tasks.put(IDLE_TASK, new TaskIdle());
	}

	private TaskRegistry() {
	}

	/**
	 * @param resourceLocation Contains the mod id as a prefix, and an identifier string for your task.
	 * @param task             Tasks are singletons.
	 */
	public static void registerTask(@Nonnull ResourceLocation resourceLocation, @Nonnull Task task) {
		if (tasks.containsKey(resourceLocation)) {
			Wizardry.LOGGER.warn("A task with a resource location " + resourceLocation.toString() + " is already registered!");
			return;
		}
		tasks.put(resourceLocation, task);
	}

	@Nonnull
	public static Task getTask(@Nonnull ResourceLocation resourceLocation) {
		if (tasks.containsKey(resourceLocation)) return tasks.get(resourceLocation);

		return tasks.get(IDLE_TASK);
	}

}
