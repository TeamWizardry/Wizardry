package com.teamwizardry.wizardry.client.proxy;

import com.teamwizardry.librarianlib.client.fx.particle.ParticleRenderDispatcher;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Config;
import com.teamwizardry.wizardry.client.core.CapeHandler;
import com.teamwizardry.wizardry.client.core.HudEventHandler;
import com.teamwizardry.wizardry.client.core.WizardryClientMethodHandles;
import com.teamwizardry.wizardry.client.fx.Shaders;
import com.teamwizardry.wizardry.client.fx.particle.LensFlareFX;
import com.teamwizardry.wizardry.client.fx.particle.MagicBurstFX;
import com.teamwizardry.wizardry.client.fx.particle.SparkleFX;
import com.teamwizardry.wizardry.client.fx.particle.trails.SparkleTrailHelix;
import com.teamwizardry.wizardry.client.render.BloodRenderLayer;
import com.teamwizardry.wizardry.client.render.glow.GlowingItemEventHandler;
import com.teamwizardry.wizardry.client.render.glow.GlowingItemRenderLayer;
import com.teamwizardry.wizardry.common.CommonProxy;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        OBJLoader.INSTANCE.addDomain(Wizardry.MODID);
        MinecraftForge.EVENT_BUS.register(new HudEventHandler());
        new WizardryClientMethodHandles(); // Load the class
        GlowingItemEventHandler.init();

        new Shaders();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ParticleRenderDispatcher.class.getName(); // load the class
        //Shaders.INSTANCE.getClass(); // ...
        //MagicBurstFX.class.getName(); // ...
        CapeHandler.INSTANCE.getClass(); // ...
        OBJLoader.INSTANCE.addDomain(Wizardry.MODID);

        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
        RenderPlayer render = skinMap.get("default");
        render.addLayer(new GlowingItemRenderLayer(render));
        render.addLayer(new BloodRenderLayer(render));

        render = skinMap.get("slim");
        render.addLayer(new GlowingItemRenderLayer(render));
        render.addLayer(new BloodRenderLayer(render));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Override
    public void openGUI(Object gui) {
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen) gui);
    }

    @Override
    public SparkleFX spawnParticleSparkle(World worldIn, Vec3d origin) {
        SparkleFX particle = new SparkleFX(worldIn, origin);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public SparkleFX spawnParticleSparkle(World worldIn, Vec3d origin, Vec3d range) {
        SparkleFX particle = new SparkleFX(worldIn, origin, range);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public SparkleTrailHelix spawnParticleSparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius, double initialTheta) {
        SparkleTrailHelix particle = new SparkleTrailHelix(worldIn, origin, center, radius, initialTheta);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public SparkleTrailHelix spawnParticleSparkleTrailHelix(World worldIn, Vec3d origin, Vec3d center, double radius) {
        SparkleTrailHelix particle = new SparkleTrailHelix(worldIn, origin, center, radius);
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
    public int getParticleDensity() {
        //2 is minimal, so 0%
        //1 is reduced, so 50%
        //0 is all, so 100%
        return Config.particlePercentage == -1 ? (Minecraft.getMinecraft().gameSettings.particleSetting == 2 ? 0 :
                Minecraft.getMinecraft().gameSettings.particleSetting == 1 ? 50 : 100) : Config.particlePercentage;
    }

    @Override
    public SparkleFX createSparkle(World world, Vec3d origin, int age) {
        SparkleFX fx = Wizardry.proxy.spawnParticleSparkle(world, origin);
        if(Wizardry.proxy.getParticleDensity() > 0)
            if (ThreadLocalRandom.current().nextInt(Wizardry.proxy.getParticleDensity()) / 100 <= 1) {
                fx.setMaxAge(age * Wizardry.proxy.getParticleDensity() / 100);
            } else fx.setMaxAge(0);
        return fx;
    }

    @Override
    public SparkleFX createSparkle(World world, Vec3d origin, Vec3d range, int age) {
        SparkleFX fx = Wizardry.proxy.spawnParticleSparkle(world, origin, range);
        if(Wizardry.proxy.getParticleDensity() > 0)
            if (ThreadLocalRandom.current().nextInt(Wizardry.proxy.getParticleDensity()) / 100 <= 1) {
                fx.setMaxAge(age * Wizardry.proxy.getParticleDensity() / 100);
            } else fx.setMaxAge(0);
        return fx;
    }

    //Wizardry.proxy.<SparkleFX>createParticle(world, origin, age)
    /*public <T extends Particle> T createParticle(World world, Vec3d origin, int age) {
        //T fx = T.
        if(Wizardry.proxy.getParticleDensity() > 0)
            if (ThreadLocalRandom.current().nextInt(Wizardry.proxy.getParticleDensity()) / 100 <= 1) {
                fx.setMaxAge(age * Wizardry.proxy.getParticleDensity() / 100);
            } else fx.setMaxAge(0);
        return fx;
    }*/
}
