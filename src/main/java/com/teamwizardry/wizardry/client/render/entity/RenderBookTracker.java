package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.EntitySpellCodex;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Created by Saad on 8/25/2016.
 */
public class RenderBookTracker extends Render<EntitySpellCodex> {

	public RenderBookTracker(RenderManager renderManager) {
		super(renderManager);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntitySpellCodex entity) {
		return null;
	}
}
