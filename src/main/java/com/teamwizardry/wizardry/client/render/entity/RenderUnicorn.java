package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.entity.EntityUnicorn;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RenderUnicorn extends RenderLiving<EntityUnicorn> {
	
	private static final ResourceLocation UNICORN_TEX_PATH = new ResourceLocation(Wizardry.MODID, "textures/entity/unicorn.png");
	
	public RenderUnicorn(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.75F);
	}
	
	@Override
	protected void preRenderCallback(EntityUnicorn entitylivingbaseIn, float partialTickTime) {
		super.preRenderCallback(entitylivingbaseIn, partialTickTime);
	}

	@Override
	public void doRender(@NotNull EntityUnicorn entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(@NotNull EntityUnicorn entity) {
		return UNICORN_TEX_PATH;
	}
}
