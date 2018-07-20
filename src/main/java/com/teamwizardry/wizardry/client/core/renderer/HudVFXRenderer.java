package com.teamwizardry.wizardry.client.core.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

//@Mod.EventBusSubscriber(modid = Wizardry.MODID)
//@SideOnly(Side.CLIENT)
public class HudVFXRenderer {

	private static Set<VFXObject> vfxObjects = new HashSet<>();

	public static void addVFX(World world, int maxTicks, Consumer<RenderGameOverlayEvent.Post> renderer) {
		vfxObjects.add(new VFXObject(world, maxTicks, renderer));
	}

	@SubscribeEvent
	public static void renderHud(RenderGameOverlayEvent.Post event) {
		ScaledResolution resolution = event.getResolution();
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();
		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;
		if (player == null || world == null) return;

		vfxObjects.removeIf(vfxObject -> {
			long lastTime = vfxObject.lastTime;
			long currentTime = world.getTotalWorldTime();
			if (currentTime - lastTime > vfxObject.maxTime) return true;
			vfxObject.renderer.accept(event);
			return false;
		});

	}

	public static class VFXObject {

		private final long lastTime;
		private final Consumer<RenderGameOverlayEvent.Post> renderer;
		private int maxTime;

		public VFXObject(World world, int maxTime, Consumer<RenderGameOverlayEvent.Post> renderer) {

			this.lastTime = world.getTotalWorldTime();
			this.maxTime = maxTime;
			this.renderer = renderer;
		}
	}
}
