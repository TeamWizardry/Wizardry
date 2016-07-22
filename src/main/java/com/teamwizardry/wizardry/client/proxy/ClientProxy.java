package com.teamwizardry.wizardry.client.proxy;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleRenderDispatcher;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.core.CapeHandler;
import com.teamwizardry.wizardry.client.core.HudEventHandler;
import com.teamwizardry.wizardry.client.fx.particle.FireFX;
import com.teamwizardry.wizardry.client.fx.particle.LensFlareFX;
import com.teamwizardry.wizardry.client.fx.particle.MagicBurstFX;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.client.fx.particle.trails.SparkleTrailHelix;
import com.teamwizardry.wizardry.common.proxy.CommonProxy;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        OBJLoader.INSTANCE.addDomain(Wizardry.MODID);
        MinecraftForge.EVENT_BUS.register(new HudEventHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ModItems.initColors();
        ParticleRenderDispatcher.class.getName(); // load the class
        //Shaders.INSTANCE.getClass(); // ...
        //MagicBurstFX.class.getName(); // ...
        CapeHandler.INSTANCE.getClass(); // ...
        OBJLoader.INSTANCE.addDomain(Wizardry.MODID);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Override
    public void loadModels() {
        ModItems.initModels();
        ModBlocks.initModels();
    }

    @Override
    public void openGUI(Object gui) {
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen) gui);
    }

    @Override
    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age, boolean fadeOut) {
        SparkleFX particle = new SparkleFX(world, x, y, z, alpha, scale, age, fadeOut);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age, double rangeX, double rangeY, double rangeZ, boolean fadeOut) {
        SparkleFX particle = new SparkleFX(world, x, y, z, alpha, scale, age, rangeX, rangeY, rangeZ, fadeOut);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public SparkleTrailHelix spawnParticleSparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius, double initialTheta, int age, boolean fade) {
        SparkleTrailHelix particle = new SparkleTrailHelix(worldIn, origin, center, radius, initialTheta, age, fade);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public SparkleTrailHelix spawnParticleSparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius, int age, boolean fade) {
        SparkleTrailHelix particle = new SparkleTrailHelix(worldIn, origin, center, radius, age, fade);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public MagicBurstFX spawnParticleMagicBurst(World world, double x, double y, double z) {
        MagicBurstFX particle = new MagicBurstFX(world, x, y, z);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public LensFlareFX spawnParticleLensFlare(World world, Vec3d pos, int age, double range) {
        LensFlareFX particle = new LensFlareFX(world, pos, age, range);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public FireFX spawnParticleFire(World world, Vec3d pos, int age, double range) {
        FireFX particle = new FireFX(world, pos, age, range);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }
}
