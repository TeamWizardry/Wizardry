package com.teamwizardry.wizardry.api.task;

import com.teamwizardry.wizardry.api.StringConsts;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class TaskWait extends Task {

	int seconds = 0;
	long initialMillis;

	public TaskWait() {
	}

	public TaskWait(int seconds) {
		this.seconds = seconds;
	}

	@Override
	public <R extends Entity & IRobot> void onStart(R robotEntity, TaskController controller) {
		this.initialMillis = System.currentTimeMillis();
	}

	@Override
	public <R extends Entity & IRobot> void onTick(R robotEntity, TaskController controller) {
		long lastTime = System.currentTimeMillis();

		if (lastTime - initialMillis >= seconds * 1000) {
			controller.next(robotEntity);
		}
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
		CompoundNBT nbt = new CompoundNBT();

		nbt.putLong(StringConsts.INITIAL_MILLIS, initialMillis);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

		if (nbt.contains(StringConsts.INITIAL_MILLIS))
			initialMillis = nbt.getLong(StringConsts.INITIAL_MILLIS);
	}
}
