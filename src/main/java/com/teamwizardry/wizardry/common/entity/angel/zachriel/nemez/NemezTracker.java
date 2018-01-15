package com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class NemezTracker {

	private NemezManager manager = new NemezManager();
	private Set<String> trackingEntities = new HashSet<>();

	public Set<Entity> getTrackedEntities(World world) {
		Set<Entity> tracking = new HashSet<>();
		for (String id : trackingEntities) {
			Entity entity = null;
			for (Entity inWorld : world.loadedEntityList) if (id.equals(inWorld.getCachedUniqueIdString())) {
				entity = inWorld;
				break;
			}
			if (entity != null)
				tracking.add(entity);
		}
		return tracking;
	}

	public void trackBlock(BlockPos pos, IBlockState state) {
		manager.pushBlockData(pos, state);
	}

	public void trackEntity(Entity entity) {
		trackingEntities.add(entity.getCachedUniqueIdString());
		manager.pushEntityData(entity);
	}

	public boolean hasNext() {
		return manager.peekAtMoment() != null;
	}

	public void endUpdate() {
		manager.pushMoment();
	}

	public void erase() {
		manager.erase();
	}

	public void collapse() {
		manager.collapse();
	}

	public NemezTracker snapshot() {
		NemezTracker manager = new NemezTracker();
		manager.manager = this.manager.snapshot();
		manager.trackingEntities = new HashSet<>(this.trackingEntities);
		return manager;
	}

	public void applySnapshot(World world) {
		manager.popMoment().apply(world, getTrackedEntities(world));
	}

	public void applySnapshot(World world, float partialTicks) {
		if (partialTicks == 0) {
			applySnapshot(world);
			return;
		}
		manager.peekAtMoment().apply(world, getTrackedEntities(world), partialTicks);
	}
}
