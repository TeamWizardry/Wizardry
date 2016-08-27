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

/**
 * Created by LordSaad44
 */
public class WorldProviderUnderWorld extends WorldProvider {

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorUnderWorld(worldObj);
    }

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

    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        return new Vec3d(0, 180, 50);
    }


    @Override
    public String getSaveFolder() {
        return "underworld";
    }
}
