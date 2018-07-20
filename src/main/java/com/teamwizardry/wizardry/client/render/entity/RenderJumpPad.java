package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.EntityJumpPad;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

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

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(@NotNull EntityJumpPad entity) {
		return null;
	}
}
