package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.angel.zachriel.EntityCorruptionProjectile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderZachrielCorruption extends Render<EntityCorruptionProjectile> {

	public RenderZachrielCorruption(RenderManager renderManager) {
		super(renderManager);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityCorruptionProjectile entity) {
		return null;
	}
}
