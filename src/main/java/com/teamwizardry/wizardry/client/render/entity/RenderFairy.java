package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 8/25/2016.
 */
public class RenderFairy extends RenderLiving<EntityFairy> {

	public RenderFairy(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, 0);
	}

	@Override
	public boolean canRenderName(EntityFairy entity) {
		return false;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFairy entity) {
		return null;
	}

	@Override
	public void doRender(EntityFairy entity, double x, double y, double z, float entityYaw, float partialTicks) {
	}
}