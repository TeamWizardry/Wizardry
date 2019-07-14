package com.teamwizardry.wizardry.api.entity.fairy.fairytasks;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class FairyTaskIdle extends FairyTask {

	@Override
	public int getPriority() {
		return -1;
	}

	@Override
	public void onStart(EntityFairy fairy) {

	}

	@Override
	public void onTick(EntityFairy fairy) {

	}

	@Override
	public void onEnd(EntityFairy fairy) {

	}

	@Override
	public void onForceTrigger(EntityFairy fairy) {

	}

	@Override
	public void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec) {

	}
}
