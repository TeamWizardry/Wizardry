package com.teamwizardry.wizardry.common.world;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad44
 */
public class WorldProviderUnderWorld extends WorldProvider {

	@NotNull
	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkGeneratorUnderWorld(world);
	}

	@NotNull
	@Override
	public DimensionType getDimensionType() {
		return Wizardry.underWorld;
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}

	@Override
	public boolean isSurfaceWorld() {
		return true;
	}

	@NotNull
	@Override
	public String getSaveFolder() {
		return "underworld";
	}

	@Override
	public void onWorldUpdateEntities() {
		setWorldTime(getWorldTime() + 5);
	}
}
