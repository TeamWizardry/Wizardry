package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.task.Task;
import com.teamwizardry.wizardry.common.core.TaskMove;
import net.minecraftforge.registries.IForgeRegistry;

public class TaskInit {
	public static void init(IForgeRegistry<Task> registry) {
		registry.registerAll(new TaskMove().setRegistryName(Wizardry.MODID, "move"));
	}
}
