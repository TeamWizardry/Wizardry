package com.teamwizardry.wizardry.common.entity.angel.zachriel;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ZachHourGlass {

	private EntityZachriel entityZachriel;

	private boolean tracking = false;

	public ZachHourGlass(@NotNull EntityZachriel entityZachriel) {
		this.entityZachriel = entityZachriel;
	}

	public void trackBlockTick(BlockPos pos, IBlockState state) {
		if (!tracking) return;
		entityZachriel.nemezDrive.trackBlock(pos, state);
	}

	public Set<Entity> getAllTrackedEntities() {
		return entityZachriel.nemezDrive.getTrackedEntities(entityZachriel.world);
	}

	public EntityZachriel getEntityZachriel() {
		return entityZachriel;
	}
}
