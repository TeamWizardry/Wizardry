package com.teamwizardry.wizardry.api.entity.fairy.fairytasks;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public abstract class FairyTask {

	public int phase = 0;

	public boolean isTaskChoked = true;

	/**
	 * Do things to the fairy and the world when the task is executed. shouldTrigger is not the only method that
	 * can trigger it. Some other tasks can trigger it as well.
	 */
	public abstract void onTrigger(EntityFairy fairy);

	/**
	 * You may add additional processing to the fairy whenever it is reconfigured with a bell.
	 *
	 * @param fairy       The fairy entity.
	 * @param targetBlock The target block configured to. Can be null (target entity instead).
	 * @param lookVec     The target look vector set to. This can be the same as before, but can also change.
	 */
	public abstract void onConfigure(EntityFairy fairy, @Nullable BlockPos targetBlock, @Nullable Entity targetEntity, Vec3d lookVec);

	/**
	 * Apply conditions for if the fairy should trigger. Checked per tick unless the task is already running.
	 */
	public abstract boolean shouldTrigger(EntityFairy fairy);
}
