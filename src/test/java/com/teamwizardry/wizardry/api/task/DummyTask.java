package com.teamwizardry.wizardry.api.task;

import com.teamwizardry.wizardry.api.StringConsts;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

import static com.teamwizardry.wizardry.api.task.TaskTests.DUMMY_TASK_LOC;

public class DummyTask extends Task {

	// TEST DATA. NOT RELEVANT.
	private BlockPos targetBlock;
	private Direction direction;

	public DummyTask() {
	}

	public DummyTask(BlockPos targetBlock, Direction direction) {
		this.targetBlock = targetBlock;
		this.direction = direction;
	}

	@Override
	public ResourceLocation getResourceLocation() {
		return DUMMY_TASK_LOC;
	}

	@Override
	public <R extends Entity & IRobot> void onStart(R robotEntity, TaskController controller) {
		controller.getStorage().storageNBT.putString("test_key", "test_value");
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
		CompoundNBT nbt = new CompoundNBT();

		nbt.putLong(StringConsts.BLOCK_POS, targetBlock.toLong());
		nbt.putInt(StringConsts.DIRECTION, direction.getIndex());

		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		if (nbt.contains(StringConsts.BLOCK_POS) && nbt.contains(StringConsts.DIRECTION)) {
			targetBlock = BlockPos.fromLong(nbt.getLong(StringConsts.BLOCK_POS));
			direction = Direction.byIndex(nbt.getInt(StringConsts.DIRECTION));
		}
	}
}
