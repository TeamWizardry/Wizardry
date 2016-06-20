package me.lordsaad.wizardry;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraft.world.World;

import me.lordsaad.wizardry.particles.MagicBurstFX;
import me.lordsaad.wizardry.particles.SparkleFX;

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

    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age) {
        return null;
    }

    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age, double rangeX, double rangeY, double rangeZ) {
        return null;
    }
    
    public MagicBurstFX spawnParticleMagicBurst(World world, double x, double y, double z) {
        return null;
    }
}
