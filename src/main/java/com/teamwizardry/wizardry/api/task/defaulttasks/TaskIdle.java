package com.teamwizardry.wizardry.api.task.defaulttasks;

import com.teamwizardry.wizardry.api.ResourceConsts;
import com.teamwizardry.wizardry.api.task.IRobot;
import com.teamwizardry.wizardry.api.task.Task;
import com.teamwizardry.wizardry.api.task.TaskController;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class TaskIdle extends Task {

	@Override
	public ResourceLocation getResourceLocation() {
		return ResourceConsts.TASK_IDLE;
	}

	@Override
	public <R extends Entity & IRobot> void onStart(R robotEntity, TaskController controller) {

	}

	@Override
	public <R extends Entity & IRobot> void onTick(R robotEntity, TaskController controller) {

	}

	@Override
	public <R extends Entity & IRobot> void onEnd(R robotEntity, TaskController controller) {

	}

	@Override
	public <R extends Entity & IRobot> void onTrigger(R robot, TaskController controller) {

	}

	@Override
	public <R extends Entity & IRobot> void onConfigure(R robotEntity, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec, TaskController controller) {

	}

	@Override
	public CompoundNBT serializeNBT() {
		return new CompoundNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}
}
