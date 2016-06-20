package me.lordsaad.wizardry.particles;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.profiler.Profiler;

public enum ParticleRenderDispatcher {
	INSTANCE;
	
	private ParticleRenderDispatcher() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	List<ParticleRenderQueue> queues = new ArrayList<>();
	
	public void addQueue(ParticleRenderQueue queue) {
		queues.add(queue);
	}
	
	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event) {
		Profiler profiler = Minecraft.getMinecraft().mcProfiler;

		profiler.startSection("wizardry-particles");
		
		for (ParticleRenderQueue queue : queues) {
			profiler.startSection(queue.name());
			queue.dispatchQueuedRenders(Tessellator.getInstance());
			profiler.endSection();
		}
		
		profiler.endSection();
		
	}
	
}
