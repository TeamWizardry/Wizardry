package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by Demoniaque on 8/25/2016.
 */
public class RenderFairy extends RenderLiving<EntityFairy> {

	public RenderFairy(RenderManager renderManager, ModelBase modelBase) {
		super(renderManager, modelBase, 0.0f);
	}

	@Override
	public boolean canRenderName(EntityFairy entity) {
		return false;
	}

	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntityFairy entity) {
		return null;
	}

	@Override
	public void doRender(@Nonnull EntityFairy entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		LibParticles.FAIRY_HEAD(entity.getEntityWorld(), entity.getPositionVector().addVector(0, 0.25, 0), entity.getColor());
		LibParticles.FAIRY_TRAIL(entity.getEntityWorld(), entity.getPositionVector().addVector(0, 0.25, 0), entity.getColor(), entity.isSad(), new Random(entity.getUniqueID().hashCode()).nextInt(150));


	}
}
