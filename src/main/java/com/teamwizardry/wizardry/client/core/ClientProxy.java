package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.fx.Shaders;
import com.teamwizardry.wizardry.client.render.BloodRenderLayer;
import com.teamwizardry.wizardry.client.render.TileStaffRenderer;
import com.teamwizardry.wizardry.client.render.glow.GlowingItemEventHandler;
import com.teamwizardry.wizardry.client.render.glow.GlowingItemRenderLayer;
import com.teamwizardry.wizardry.common.core.CommonProxy;
import com.teamwizardry.wizardry.common.tile.TileStaff;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		OBJLoader.INSTANCE.addDomain(Wizardry.MODID);
		MinecraftForge.EVENT_BUS.register(new HudEventHandler());
		new WizardryClientMethodHandles(); // Load the class
		GlowingItemEventHandler.init();

		new Shaders();

		ModBlocks.initModel();
		ModEntities.initModels();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		ClientRegistry.bindTileEntitySpecialRenderer(TileStaff.class, new TileStaffRenderer());

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

	@Override
	public void onResourceManagerReload(@NotNull IResourceManager resourceManager) {
		MinecraftForge.EVENT_BUS.post(new ResourceReloadEvent(resourceManager));
	}

	public static class ResourceReloadEvent extends Event {
		public final IResourceManager resourceManager;

		public ResourceReloadEvent(IResourceManager manager) {
			resourceManager = manager;
		}
	}
}
