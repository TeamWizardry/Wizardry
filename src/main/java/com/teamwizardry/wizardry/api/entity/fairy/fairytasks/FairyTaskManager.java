package com.teamwizardry.wizardry.api.entity.fairy.fairytasks;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class FairyTaskManager {

	public static final FairyTaskManager INSTANCE = new FairyTaskManager();

	private final Set<FairyTask> tasks = new HashSet<>();

	private FairyTaskManager() {
		tasks.add(new FairyTaskGrabItems());
	}

	public Set<FairyTask> getTasks() {
		return tasks;
	}

	@Nullable
	public FairyTask getTaskForItemStack(ItemStack stack) {
		for (FairyTask fairyTask : tasks) {
			if (ItemStack.areItemsEqual(fairyTask.getFoodItem(), stack)) {
				return fairyTask;
			}
		}

		return null;
	}

	@Nullable
	public FairyTask getTaskFromKey(String key) {
		for (FairyTask fairyTask : tasks) {
			if (fairyTask.getNBTKey().equals(key)) return fairyTask;
		}

		return null;
	}
}
