package com.teamwizardry.wizardry.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;

import java.util.ArrayDeque;
import java.util.Queue;

public abstract class ParticleRenderQueue<T extends Particle> {

    protected Queue<T> renderQueue = new ArrayDeque<>();

    public ParticleRenderQueue() {
        ParticleRenderDispatcher.INSTANCE.addQueue(this);
    }

    public void add(T particle) {
        renderQueue.add(particle);
    }

    public abstract String name();

    public abstract void dispatchQueuedRenders(Tessellator tessellator);

}
