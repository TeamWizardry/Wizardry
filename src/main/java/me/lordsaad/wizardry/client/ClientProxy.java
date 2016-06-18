package me.lordsaad.wizardry.client;

import me.lordsaad.wizardry.CommonProxy;
import me.lordsaad.wizardry.ModBlocks;
import me.lordsaad.wizardry.ModItems;
import me.lordsaad.wizardry.particles.SparkleFX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void init(FMLInitializationEvent event) {
        ModItems.initColors();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {

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

    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age) {
        SparkleFX particle = new SparkleFX(world, x, y, z, alpha, scale, age);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }

    public SparkleFX spawnParticleSparkle(World world, double x, double y, double z, float alpha, float scale, int age, double rangeX, double rangeY, double rangeZ) {
        SparkleFX particle = new SparkleFX(world, x, y, z, alpha, scale, age, rangeX, rangeY, rangeZ);
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }
}
