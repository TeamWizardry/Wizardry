package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.EntityZachrielCorruption;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderZachrielCorruption extends RenderLiving<EntityZachrielCorruption> {

	public RenderZachrielCorruption(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.0f);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityZachrielCorruption entity) {
		return null;
	}
}
