package com.teamwizardry.wizardry.client.render.entity;

import com.teamwizardry.wizardry.common.entity.EntitySpellCodex;
import com.teamwizardry.wizardry.common.fluid.FluidMana;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Saad on 8/25/2016.
 */
public class RenderSpellCodex extends Render<EntitySpellCodex> {

	public RenderSpellCodex(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(@Nonnull EntitySpellCodex entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		if (entity.getEntityWorld().getBlockState(entity.getPosition()).getBlock() == FluidMana.instance.getBlock()) {
			if (entity.expiry > 0) {
				LibParticles.BOOK_BEAM_NORMAL(entity.getEntityWorld(), entity.getPositionVector());
				LibParticles.BOOK_BEAM_HELIX(entity.getEntityWorld(), entity.getPositionVector());
			} else {
				LibParticles.BOOK_LARGE_EXPLOSION(entity.getEntityWorld(), entity.getPositionVector());
			}
		}
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(@Nonnull EntitySpellCodex entity) {
		return null;
	}
}
