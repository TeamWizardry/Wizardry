package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.projectile.EntitySpellProjectile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 8/25/2016.
 */
public class RenderSpellProjectile extends Render<EntitySpellProjectile> {

	public RenderSpellProjectile(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(@Nonnull EntitySpellProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntitySpellProjectile entity) {
		return null;
	}
}
