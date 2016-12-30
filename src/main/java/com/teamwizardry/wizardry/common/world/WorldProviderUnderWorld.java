package com.teamwizardry.wizardry.common.world;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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

	@Override
	public float calculateCelestialAngle(long par1, float par3) {
		return 0.0F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float par1, float par2) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored() {
		return true;
	}

    @NotNull
    @Override
    public Vec3d getSkyColor(@NotNull Entity cameraEntity, float partialTicks) {
        return new Vec3d(0, 180, 50);
	}


    @NotNull
    @Override
	public String getSaveFolder() {
		return "underworld";
	}
}
