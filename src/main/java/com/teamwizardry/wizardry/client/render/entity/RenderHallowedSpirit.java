package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.entity.EntityHallowedSprit;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Saad on 8/21/2016.
 */
public class RenderHallowedSpirit extends RenderLiving<EntityHallowedSprit> {

	public RenderHallowedSpirit(RenderManager renderManager, ModelBase modelBase, float shadowSize) {
		super(renderManager, modelBase, shadowSize);
	}

	@Override
	public boolean canRenderName(EntityHallowedSprit entity) {
		return false;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityHallowedSprit entity) {
		return new ResourceLocation(Wizardry.MODID + "textures/entity/hallowed_spirit.png");
	}
}