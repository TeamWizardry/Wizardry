package com.teamwizardry.wizardry.common.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by LordSaad44
 */
public class UnderWorldProvider extends WorldProvider {

	public static int id = DimensionManager.getNextFreeDimId();

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderUnderWorld(worldObj, 3242351, true, "null");
	}


	@Override
	public DimensionType getDimensionType() {
		return DimensionType.register("underworld", "world", id, UnderWorldProvider.class, false);
	}
}
