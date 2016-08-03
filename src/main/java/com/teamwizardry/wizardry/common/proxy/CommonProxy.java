package com.teamwizardry.wizardry.common.proxy;

import com.teamwizardry.wizardry.api.Config;
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

    public SparkleFX spawnParticleSparkle(World worldIn, Vec3d origin) {
        return null;
    }

    public SparkleFX spawnParticleSparkle(World worldIn, Vec3d origin, Vec3d range) {
        return null;
    }

    public SparkleTrailHelix spawnParticleSparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius, double initialTheta) {
        return null;
    }

    public SparkleTrailHelix spawnParticleSparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius) {
        return null;
    }

    public LensFlareFX spawnParticleLensFlare(World worldIn, Vec3d pos, int age, double range) {
        return null;
    }

    public MagicBurstFX spawnParticleMagicBurst(World world, double x, double y, double z) {
        return null;
    }

    public int getParticleDensity() {
        if(Config.particlePercentage <= 0) return 100;
        else return Config.particlePercentage;
        //not that it matters, it should never be called on server side
    }

    public SparkleFX createSparkle(World world, Vec3d origin, Vec3d range, int age) {
        return null;
    }

    public SparkleFX createSparkle(World world, Vec3d origin, int age) {
        return null;
    }

}
