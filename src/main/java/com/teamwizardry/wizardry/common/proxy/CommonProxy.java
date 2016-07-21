package com.teamwizardry.wizardry.common.proxy;

import com.teamwizardry.wizardry.client.fx.particle.LensFlareFX;
import com.teamwizardry.wizardry.client.fx.particle.MagicBurstFX;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.client.fx.particle.trails.SparkleTrailHelix;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {

    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public boolean isClient() {
        return false;
    }

    public void openGUI(Object gui) {

    }

    public void loadModels() {

    }

    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age, boolean fadeOut) {
        return null;
    }

    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age, double rangeX, double rangeY, double rangeZ, boolean fadeOut) {
        return null;
    }

    public SparkleTrailHelix spawnParticleSparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius, double initialTheta, int age, boolean fade) {
        return null;
    }

    public SparkleTrailHelix spawnParticleSparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius, int age, boolean fade) {
        return null;
    }

    public LensFlareFX spawnParticleLensFlare(World worldIn, Vec3d pos, int age, double range) {
        return null;
    }

    public MagicBurstFX spawnParticleMagicBurst(World world, double x, double y, double z) {
        return null;
    }
}
