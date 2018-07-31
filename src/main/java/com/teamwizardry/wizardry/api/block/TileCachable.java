package com.teamwizardry.wizardry.api.block;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.chunk.WizardryChunk;
import com.teamwizardry.wizardry.api.capability.chunk.WizardryChunkCapability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileCachable extends TileMod
{
	@Nonnull
	public WeakHashMap<TileEntity, Double> distanceCache = new WeakHashMap<>();
	
	private static int LINK_DIST_SQ = ConfigValues.networkLinkDistance * ConfigValues.networkLinkDistance;
	private static int CHUNK_RANGE = (ConfigValues.networkLinkDistance >> 4) + 1;
	private static int CHUNK_DIST_SQ = CHUNK_RANGE * CHUNK_RANGE;

	@Override
	public void onLoad()
	{
		super.onLoad();

		int chunkX = pos.getX() >> 4;
		int chunkZ = pos.getZ() >> 4;

		Queue<TileCachable> toCheck = new LinkedList<>();
		
		for (int x = -CHUNK_RANGE; x <= CHUNK_RANGE; x++)
		{
			for (int z = -CHUNK_RANGE; z <= CHUNK_RANGE; z++)
			{
				if (x*x + z*z > CHUNK_DIST_SQ) continue;
				WizardryChunk chunk = WizardryChunkCapability.get(world.getChunkFromChunkCoords(chunkX + x, chunkZ + z));
				toCheck.addAll(chunk.getCachableTiles());
			}
		}
		
		while (!toCheck.isEmpty())
		{
			TileCachable tile = toCheck.remove();
			double dist = tile.getPos().distanceSq(pos);
			if (dist <= LINK_DIST_SQ)
			{
				tile.distanceCache.put(this, dist);
				distanceCache.put(tile, dist);
			}
		}
		
		WizardryChunk chunk = WizardryChunkCapability.get(world.getChunkFromChunkCoords(chunkX, chunkZ));
		chunk.addCachableTile(this);
		this.onBreak();
		this.onChunkUnload();
	}

	public double getCachedDistanceSq(TileEntity tile) {
		return distanceCache.getOrDefault(tile, Double.MAX_VALUE);
	}

	@Nonnull
	@SuppressWarnings("unchecked")
	public <T extends TileEntity> Set<T> getNearestTiles(Class<T> clazz) {
		Set<T> poses = new HashSet<>();
		for (TileEntity tile : distanceCache.keySet()) {
			if (tile == this) continue;
			if (!world.isBlockLoaded(tile.getPos())) continue;
			if (!tile.getClass().isAssignableFrom(clazz)) continue;

			poses.add((T) tile);
		}
		return poses;
	}

	@Nullable
	public <T extends TileEntity> BlockPos getNearestTilePos(Class<T> clazz) {
		BlockPos closestTile = null;

		double lastDist = Double.MAX_VALUE;
		for (TileEntity tile : distanceCache.keySet()) {
			if (tile == this) continue;
			if (!world.isBlockLoaded(tile.getPos())) continue;
			if (!tile.getClass().isAssignableFrom(clazz)) continue;

			double distSQ = distanceCache.get(tile);
			if (lastDist > distSQ) {
				lastDist = distSQ;
				closestTile = tile.getPos();
			}
		}
		return closestTile;
	}
}
