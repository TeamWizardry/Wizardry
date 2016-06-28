package com.teamwizardry.wizardry.client.proxy;

import com.teamwizardry.wizardry.api.util.gui.ScissorUtil;
import com.teamwizardry.wizardry.client.core.HudEventHandler;
import com.teamwizardry.wizardry.client.particle.MagicBurstFX;
import com.teamwizardry.wizardry.client.particle.ParticleRenderDispatcher;
import com.teamwizardry.wizardry.client.particle.SparkleFX;
import com.teamwizardry.wizardry.client.particle.shader.ShaderHelper;
import com.teamwizardry.wizardry.common.proxy.CommonProxy;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ShaderHelper.initShaders();
        MinecraftForge.EVENT_BUS.register(new HudEventHandler());
        MinecraftForge.EVENT_BUS.register(ScissorUtil.INSTANCE);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ModItems.initColors();
        ParticleRenderDispatcher.class.getName(); // load the class
        MagicBurstFX.class.getName(); // ...
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

    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age, boolean fadeOut) {
        SparkleFX particle = new SparkleFX(world, x, y, z, alpha, scale, age, fadeOut);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age, double rangeX, double rangeY, double rangeZ, boolean fadeOut) {
        SparkleFX particle = new SparkleFX(world, x, y, z, alpha, scale, age, rangeX, rangeY, rangeZ, fadeOut);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    @Override
    public MagicBurstFX spawnParticleMagicBurst(World world, double x, double y, double z) {
        MagicBurstFX particle = new MagicBurstFX(world, x, y, z);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }
}