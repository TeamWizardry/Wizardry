package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.EntityDevilDust;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nonnull;

import javax.annotation.Nullable;

/**
 * Created by Saad on 8/25/2016.
 */
public class RenderDevilDust extends Render<EntityDevilDust> {

	public RenderDevilDust(RenderManager renderManager) {
		super(renderManager);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityDevilDust entity) {
		return null;
	}
}
