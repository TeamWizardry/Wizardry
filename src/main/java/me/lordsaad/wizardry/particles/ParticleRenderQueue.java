package me.lordsaad.wizardry.particles;

import java.util.ArrayDeque;
import java.util.Queue;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;

public abstract class ParticleRenderQueue<T extends Particle> {

	public ParticleRenderQueue() {
		ParticleRenderDispatcher.INSTANCE.addQueue(this);
	}
	
	protected Queue<T> renderQueue = new ArrayDeque<>();
	
	public void add(T particle) {
		renderQueue.add(particle);
	}
	
	public abstract String name();
	
	public abstract void dispatchQueuedRenders(Tessellator tessellator);

}
