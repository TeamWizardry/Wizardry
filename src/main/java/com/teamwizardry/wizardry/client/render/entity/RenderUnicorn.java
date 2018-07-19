package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.entity.EntityUnicorn;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RenderUnicorn extends RenderLiving<EntityUnicorn> {

	private static final ResourceLocation UNICORN_TEX_PATH = new ResourceLocation(Wizardry.MODID, "textures/entity/unicorn.png");

	public RenderUnicorn(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.75F);
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityUnicorn entity) {
		return UNICORN_TEX_PATH;
	}
}
