package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.wizardry.api.ConfigValues;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

public class TileCachable extends TileMod {

	private static WeakHashMap<TileEntity, WeakHashMap<TileEntity, Double>> TILE_CACHE = new WeakHashMap<>();
	@Nonnull
	public WeakHashMap<TileEntity, Double> distanceCache = new WeakHashMap<>();

	@Override
	public void onLoad() {
		super.onLoad();

		TILE_CACHE.putIfAbsent(this, distanceCache);

		if (distanceCache.isEmpty() && TILE_CACHE.size() > 1) {

			for (TileEntity key : TILE_CACHE.keySet()) {
				if (key == this) continue;

				double distance = key.getPos().distanceSq(getPos());
				if (distance <= ConfigValues.networkLinkDistance * ConfigValues.networkLinkDistance) {
					distanceCache.put(key, distance);

					WeakHashMap<TileEntity, Double> otherDistanceCache = TILE_CACHE.get(key);
					if (otherDistanceCache != null) {
						otherDistanceCache.put(this, distance);
					}
				}
			}
		}
	}

	public double getCachedDistanceSq(TileEntity tile) {
		return distanceCache.getOrDefault(tile, Double.MAX_VALUE);
	}

	@Nonnull
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
