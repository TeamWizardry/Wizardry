package com.teamwizardry.wizardry.common.entity.angel.zachriel.nemez;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashSet;
import java.util.Set;

public class NemezArenaTracker implements INBTSerializable<NBTTagList> {

	public static final int MAXIMUM_MOMENTS = 6000;

	private NemezManager manager = new NemezManager();
	private Set<String> trackingEntities = new HashSet<>();

	public Set<Entity> getTrackedEntities(World world) {
		Set<Entity> tracking = new HashSet<>();
		for (Entity inWorld : world.loadedEntityList)
			if (trackingEntities.contains(inWorld.getCachedUniqueIdString()))
				tracking.add(inWorld);
		return tracking;
	}

	public void trackBlock(BlockPos pos, IBlockState state) {
		manager.pushBlockData(pos, state);
		compressDownTo(MAXIMUM_MOMENTS);
	}

	public void trackEntity(Entity entity) {
		trackingEntities.add(entity.getCachedUniqueIdString());
		manager.pushEntityData(entity);
		compressDownTo(MAXIMUM_MOMENTS);
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

	public boolean needsCompression(int maximumMoments) {
		return manager.needsCompression(maximumMoments);
	}

	public void compressDownTo(int maximumMoments) {
		if (needsCompression(maximumMoments))
			manager.compressDownTo(maximumMoments);
	}

	public NemezArenaTracker snapshot() {
		compressDownTo(MAXIMUM_MOMENTS);
		NemezArenaTracker manager = new NemezArenaTracker();
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

	@Override
	public NBTTagList serializeNBT() {
		return manager.serializeNBT();
	}

	@Override
	public void deserializeNBT(NBTTagList nbt) {
		manager.deserializeNBT(nbt);
	}
}
