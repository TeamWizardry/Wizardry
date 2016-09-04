package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.fx.Shaders;
import com.teamwizardry.wizardry.client.render.BloodRenderLayer;
import com.teamwizardry.wizardry.client.render.TilePedestalRenderer;
import com.teamwizardry.wizardry.client.render.glow.GlowingItemEventHandler;
import com.teamwizardry.wizardry.client.render.glow.GlowingItemRenderLayer;
import com.teamwizardry.wizardry.common.core.CommonProxy;
import com.teamwizardry.wizardry.common.tile.TilePedestal;
import com.teamwizardry.wizardry.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Map;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        OBJLoader.INSTANCE.addDomain(Wizardry.MODID);
        MinecraftForge.EVENT_BUS.register(new HudEventHandler());
        new WizardryClientMethodHandles(); // Load the class
        GlowingItemEventHandler.init();

        new Shaders();

        ModEntities.initModels();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.class, new TilePedestalRenderer());

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
        super.postInit(event);
    }

    @Override
    public void openGUI(Object gui) {
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen) gui);
    }
}
