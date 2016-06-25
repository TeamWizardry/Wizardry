package com.teamwizardry.wizardry.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public enum ParticleRenderDispatcher {
    INSTANCE;

    List<ParticleRenderQueue> queues = new ArrayList<>();

    ParticleRenderDispatcher() {
        MinecraftForge.EVENT_BUS.register(this);
    }

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
