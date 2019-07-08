package com.teamwizardry.wizardry.api.entity.fairy.fairytasks;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.entity.fairy.fairytasks.FairyTaskRegistry.IDLE_TASK;

public class FairyTaskController {

	@Nonnull
	private ResourceLocation location = IDLE_TASK;

	@Nonnull
	private FairyTask task = FairyTaskRegistry.createTaskFromResource(location);

	public FairyTaskController() {
	}

	public void tick(EntityFairy fairy) {
		if (task.shouldTrigger(fairy)) {
			task.onTrigger(fairy);
		}
	}

	@Nonnull
	public FairyTask getTask() {
		return task;
	}

	public void setTask(@Nonnull ResourceLocation location) {
		this.location = location;
		this.task = FairyTaskRegistry.createTaskFromResource(location);
	}

	@Nonnull
	public ResourceLocation getLocation() {
		return location;
	}
}
