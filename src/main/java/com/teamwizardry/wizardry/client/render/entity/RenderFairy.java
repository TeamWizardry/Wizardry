package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.api.entity.fairy.FairyData;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

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
	public void doRender(@NotNull EntityFairy entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		FairyData dataFairy = entity.getDataFairy();
		if (dataFairy == null) return;

		dataFairy.render(
				entity.world,
				new Vec3d(entity.posX, entity.posY + (dataFairy.isDepressed ? entity.height : 0), entity.posZ),
				new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ),
				partialTicks);
	}
}
