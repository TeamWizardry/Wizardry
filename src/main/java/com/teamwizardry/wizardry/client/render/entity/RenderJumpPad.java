package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.EntityJumpPad;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 8/25/2016.
 */
public class RenderJumpPad extends RenderLiving<EntityJumpPad> {

	public RenderJumpPad(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.0f);
	}

	@Override
	public boolean canRenderName(EntityJumpPad entity) {
		return false;
	}

	@Nonnull
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityJumpPad entity) {
		return null;
	}

	@Override
	public void doRender(@Nonnull EntityJumpPad entity, double x, double y, double z, float entityYaw, float partialTicks) {
	}

}
